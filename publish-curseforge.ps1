param(
  [Parameter(Mandatory)] [string] $Version,
  [string] $Changelog = '',
  [int] $ProjectId = 1591249
)
$ErrorActionPreference = 'Stop'
$token = $env:CURSEFORGE_TOKEN
if (-not $token) { $token = [Environment]::GetEnvironmentVariable('CURSEFORGE_TOKEN', 'User') }
if (-not $token) { throw 'Set the CURSEFORGE_TOKEN environment variable to your CurseForge API key.' }

$libs = Join-Path $PSScriptRoot 'build\libs'
$loaders = @{ fabric = 7499; forge = 7498; neoforge = 10150 }
$client = 9638; $server = 9639; $j17 = 8326; $j21 = 11135; $j25 = 14454
$mc = @{
  '1.20.1' = @(9990); '1.21.1' = @(11779); '1.21.2' = @(12079); '1.21.4' = @(12281); '1.21.8' = @(13620)
  '1.21.9' = @(13927); '1.21.10' = @(13964); '1.21.11' = @(14406)
  '26.1.x' = @(15933, 16021, 16082); '26.2' = @(16498); '26.3' = @(17045)
}

foreach ($jar in Get-ChildItem $libs -Filter "publicskintotems-*-mc*-$Version.jar" | Sort-Object Name) {
  if ($jar.Name -notmatch '^publicskintotems-(fabric|forge|neoforge)-mc(.+)-' + [regex]::Escape($Version) + '\.jar$') { continue }
  $loader = $Matches[1]; $label = $Matches[2]
  $ids = if ($label -eq '1.21.9-1.21.10') { $mc['1.21.9'] + $mc['1.21.10'] } else { $mc[$label] }
  $java = if ($label -like '26.*') { $j25 } elseif ($label -eq '1.20.1') { $j17 } else { $j21 }
  $meta = [ordered]@{
    changelog     = $Changelog
    changelogType = 'text'
    displayName   = "PublicSkinTotems $Version ($loader $label)"
    gameVersions  = @($ids) + @($loaders[$loader], $client, $server, $java)
    releaseType   = 'release'
  }
  if ($loader -eq 'fabric') {
    $meta.relations = @{ projects = @(@{ slug = 'fabric-api'; type = 'requiredDependency' }) }
  }
  $metaFile = Join-Path $env:TEMP 'cf-meta.json'
  [IO.File]::WriteAllText($metaFile, ($meta | ConvertTo-Json -Depth 6 -Compress), (New-Object Text.UTF8Encoding $false))
  $out = & curl.exe -sS -w "`n%{http_code}" -H "X-Api-Token: $token" `
    -F "metadata=<$metaFile;type=application/json" -F "file=@$($jar.FullName)" `
    "https://minecraft.curseforge.com/api/projects/$ProjectId/upload-file"
  $code = ($out | Select-Object -Last 1)
  "$loader $label -> HTTP $code  $($out | Select-Object -First 1)"
  if ($code -ne '200') { throw "upload failed for $($jar.Name)" }
}
Remove-Item (Join-Path $env:TEMP 'cf-meta.json') -ErrorAction SilentlyContinue
