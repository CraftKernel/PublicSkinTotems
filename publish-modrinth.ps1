param(
  [Parameter(Mandatory)] [string] $Version,
  [string] $Changelog = '',
  [string] $ProjectId = '1bVuuQjy'
)
$ErrorActionPreference = 'Stop'
$token = $env:MODRINTH_TOKEN
if (-not $token) { $token = [Environment]::GetEnvironmentVariable('MODRINTH_TOKEN', 'User') }
if (-not $token) { throw 'Set the MODRINTH_TOKEN environment variable to your Modrinth API key.' }

$libs = Join-Path $PSScriptRoot 'build\libs'
$fabricApi = 'P7dR8mSH'
$mc = @{
  '1.20.1' = @('1.20.1'); '1.21.1' = @('1.21.1'); '1.21.2' = @('1.21.2'); '1.21.4' = @('1.21.4'); '1.21.8' = @('1.21.8')
  '1.21.9' = @('1.21.9'); '1.21.10' = @('1.21.10'); '1.21.9-1.21.10' = @('1.21.9', '1.21.10'); '1.21.11' = @('1.21.11')
  '26.1.x' = @('26.1', '26.1.1', '26.1.2'); '26.2' = @('26.2'); '26.3' = @('26.3')
}

foreach ($jar in Get-ChildItem $libs -Filter "publicskintotems-*-mc*-$Version.jar" | Sort-Object Name) {
  if ($jar.Name -notmatch '^publicskintotems-(fabric|forge|neoforge)-mc(.+)-' + [regex]::Escape($Version) + '\.jar$') { continue }
  $loader = $Matches[1]; $label = $Matches[2]
  $deps = @()
  if ($loader -eq 'fabric') { $deps += @{ project_id = $fabricApi; dependency_type = 'required' } }
  $data = [ordered]@{
    name           = "PublicSkinTotems $Version ($loader $label)"
    version_number = "$Version+$loader-mc$label"
    changelog      = $Changelog
    dependencies   = $deps
    game_versions  = $mc[$label]
    version_type   = 'release'
    loaders        = @($loader)
    featured       = $false
    project_id     = $ProjectId
    file_parts     = @('file')
    primary_file   = 'file'
  }
  $dataFile = Join-Path $env:TEMP 'mr-data.json'
  [IO.File]::WriteAllText($dataFile, ($data | ConvertTo-Json -Depth 6 -Compress), (New-Object Text.UTF8Encoding $false))
  $out = & curl.exe -sS -w "`n%{http_code}" -H "Authorization: $token" -H 'User-Agent: CraftKernel/PublicSkinTotems-publisher' `
    -F "data=<$dataFile;type=application/json" -F "file=@$($jar.FullName);type=application/java-archive" `
    'https://api.modrinth.com/v2/version'
  $code = ($out | Select-Object -Last 1)
  $body = ($out | Select-Object -SkipLast 1) -join ''
  "$loader $label -> HTTP $code  $($body.Substring(0, [Math]::Min(120, $body.Length)))"
  if ($code -ne '200') { throw "upload failed for $($jar.Name)" }
}
Remove-Item (Join-Path $env:TEMP 'mr-data.json') -ErrorAction SilentlyContinue
