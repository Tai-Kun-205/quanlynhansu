$root = Split-Path $PSScriptRoot
$mono = Join-Path $root 'hr-management'
$pkg  = 'src\main\java\com\example\hr_management'

$services = @{
  'auth-service' = @{ port = 8081; ddl = 'update'; init = $true
    controllers = 'Auth','User','Account','ActivityLog'
    services    = 'Auth','User','ActivityLog' }
  'hr-core-service' = @{ port = 8082; ddl = 'none'; init = $false
    controllers = 'Employee','Department','Position','Notification','Dashboard','Export'
    services    = 'Employee','Department','Position','Notification','Dashboard','ExcelExport' }
  'attendance-service' = @{ port = 8083; ddl = 'none'; init = $false
    controllers = 'Attendance','LeaveRequest','WorkShift','WorkAssignment','DemoNetwork'
    services    = 'Attendance','LeaveRequest','WorkShift','WorkAssignment','NetworkAccess','ActivityLog' }
  'payroll-service' = @{ port = 8084; ddl = 'none'; init = $false
    controllers = 'Payroll'
    services    = 'Payroll' }
}

foreach ($name in $services.Keys) {
  $cfg = $services[$name]
  $dst = Join-Path $root $name
  if (Test-Path $dst) { Remove-Item $dst -Recurse -Force }
  New-Item -ItemType Directory $dst | Out-Null
  Copy-Item (Join-Path $mono 'src') $dst -Recurse
  Remove-Item (Join-Path $dst 'src\test') -Recurse -Force -ErrorAction SilentlyContinue

  # giữ lại controller/service thuộc service này
  $keepC = $cfg.controllers | % { "${_}Controller.java" }
  Get-ChildItem (Join-Path $dst "$pkg\controller") -Filter *.java | ? { $keepC -notcontains $_.Name } | Remove-Item
  $keepS = $cfg.services | % { "${_}Service.java" }
  Get-ChildItem (Join-Path $dst "$pkg\service") -Filter *.java | ? { $keepS -notcontains $_.Name } | Remove-Item
  if (-not $cfg.init) { Remove-Item (Join-Path $dst "$pkg\config\DataInitializer.java") }

  # pom
  $pom = Get-Content (Join-Path $mono 'pom.xml') -Raw -Encoding UTF8
  $pom = $pom.Replace('<artifactId>hr-management</artifactId>', "<artifactId>$name</artifactId>").Replace('<name>hr-management</name>', "<name>$name</name>")
  Set-Content (Join-Path $dst 'pom.xml') $pom -Encoding UTF8

  # properties
  $pp = Join-Path $dst 'src\main\resources\application.properties'
  $p = Get-Content $pp -Raw
  $p = $p -replace 'spring.application.name=.*', "spring.application.name=$name"
  $p = $p -replace 'server.port=.*', "server.port=$($cfg.port)"
  $p = $p -replace 'ddl-auto=.*', "ddl-auto=$($cfg.ddl)"
  Set-Content $pp $p -Encoding UTF8
  "created $name"
}

