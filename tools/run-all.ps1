# Chạy toàn bộ hệ thống SOA (mỗi service một cửa sổ PowerShell).
# Yêu cầu: MySQL đang chạy, Java 21. Maven lấy từ PATH hoặc ~\.tools\apache-maven-3.9.9
$root = Split-Path $PSScriptRoot
$mvn = if (Get-Command mvn -ErrorAction SilentlyContinue) { 'mvn' } else { "$env:USERPROFILE\.tools\apache-maven-3.9.9\bin\mvn.cmd" }

# auth-service chạy trước vì nó tạo/cập nhật schema và dữ liệu mẫu (ddl-auto=update).
# Khoa xac thuc giua cac service: tu sinh ngau nhien neu chua dat (khong luu trong source). Cac cua so con se ke thua bien nay.
if (-not $env:SERVICE_API_KEY) { $env:SERVICE_API_KEY = [Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }) -as [byte[]]) }
$order = 'auth-service','hr-core-service','attendance-service','payroll-service','api-gateway'
foreach ($s in $order) {
  Start-Process powershell -ArgumentList '-NoExit','-Command',"`$host.UI.RawUI.WindowTitle='$s'; & '$mvn' -f '$root\$s\pom.xml' spring-boot:run"
  if ($s -eq 'auth-service') { Start-Sleep -Seconds 25 }
}
Write-Host 'Gateway: http://localhost:8080 | WSDL: http://localhost:8082/ws/employees.wsdl'

