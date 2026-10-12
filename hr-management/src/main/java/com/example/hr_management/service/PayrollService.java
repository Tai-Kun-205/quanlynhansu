package com.example.hr_management.service;
import com.example.hr_management.dto.request.PayrollRequest;
import com.example.hr_management.dto.response.PayrollResponse;
import com.example.hr_management.entity.*;
import com.example.hr_management.exception.*;
import com.example.hr_management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
@Service @RequiredArgsConstructor public class PayrollService {
 private final PayrollRepository payrolls; private final EmployeeRepository employees; private final AttendanceRepository attendances; private final UserRepository users;
 public Page<PayrollResponse> search(Integer month,Integer year,Long dept,int page,int size){return payrolls.search(null,month,year,dept,PageRequest.of(page,size)).map(this::toResponse);}
 public Page<PayrollResponse> my(String username,int page,int size){Long id=users.findByUsername(username).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy tài khoản")).getEmployee().getId();return payrolls.search(id,null,null,null,PageRequest.of(page,size)).map(this::toResponse);}
 @Transactional public PayrollResponse save(PayrollRequest r){Employee e=employees.findById(r.getEmployeeId()).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy nhân viên")); Payroll p=payrolls.findByEmployeeIdAndMonthAndYear(r.getEmployeeId(),r.getMonth(),r.getYear()).orElse(new Payroll()); if(p.getId()!=null&&p.getStatus()==Payroll.PayrollStatus.FINALIZED) throw new BusinessException("Bảng lương đã chốt, không thể chỉnh sửa"); YearMonth period=YearMonth.of(r.getYear(),r.getMonth()); List<Attendance> records=attendances.findByEmployeeIdAndWorkDateBetween(r.getEmployeeId(),period.atDay(1),period.atEndOfMonth()); int actual=(int) records.stream().filter(a->a.getStatus()==Attendance.AttendanceStatus.PRESENT||a.getStatus()==Attendance.AttendanceStatus.LATE||a.getStatus()==Attendance.AttendanceStatus.EARLY_LEAVE).count(); int late=(int) records.stream().filter(a->a.getStatus()==Attendance.AttendanceStatus.LATE).count(); int early=(int) records.stream().filter(a->a.getStatus()==Attendance.AttendanceStatus.EARLY_LEAVE).count();  int absent=(int) records.stream().filter(a->a.getStatus()==Attendance.AttendanceStatus.ABSENT).count(); BigDecimal base=z(r.getBaseSalary()); BigDecimal attendanceSalary=base.divide(BigDecimal.valueOf(r.getStandardWorkDays()),2,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(actual)).setScale(2,RoundingMode.HALF_UP); BigDecimal allowance=z(r.getAllowance()), bonus=z(r.getBonus()), deduction=z(r.getDeduction()); p.setEmployee(e);p.setMonth(r.getMonth());p.setYear(r.getYear());p.setBaseSalary(base);p.setStandardWorkDays(r.getStandardWorkDays());p.setActualWorkDays(actual);p.setAttendanceSalary(attendanceSalary);p.setLateDays(late);p.setEarlyLeaveDays(early);p.setAbsentDays(absent);p.setAllowance(allowance);p.setBonus(bonus);p.setDeduction(deduction);p.setNetSalary(attendanceSalary.add(allowance).add(bonus).subtract(deduction).setScale(2,RoundingMode.HALF_UP));return toResponse(payrolls.save(p));}
 @Transactional public void finalize(Long id){Payroll p=payrolls.findById(id).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy bảng lương"));p.setStatus(Payroll.PayrollStatus.FINALIZED);payrolls.save(p);}
 private BigDecimal z(BigDecimal x){return x==null?BigDecimal.ZERO:x;}
 private PayrollResponse toResponse(Payroll p){PayrollResponse r=new PayrollResponse();r.setId(p.getId());r.setEmployeeId(p.getEmployee().getId());r.setEmployeeCode(p.getEmployee().getCode());r.setEmployeeName(p.getEmployee().getFullName());r.setDepartmentName(p.getEmployee().getDepartment()==null?null:p.getEmployee().getDepartment().getName());r.setMonth(p.getMonth());r.setYear(p.getYear());r.setStandardWorkDays(p.getStandardWorkDays());r.setActualWorkDays(p.getActualWorkDays());r.setLateDays(p.getLateDays());r.setEarlyLeaveDays(p.getEarlyLeaveDays());r.setAbsentDays(p.getAbsentDays());r.setBaseSalary(p.getBaseSalary());r.setAttendanceSalary(p.getAttendanceSalary());r.setAllowance(p.getAllowance());r.setBonus(p.getBonus());r.setDeduction(p.getDeduction());r.setNetSalary(p.getNetSalary());r.setStatus(p.getStatus().name());return r;}
}
