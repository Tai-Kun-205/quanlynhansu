package com.example.hr_management.config;

import com.example.hr_management.entity.Department;
import com.example.hr_management.entity.Employee;
import com.example.hr_management.entity.Position;
import com.example.hr_management.entity.User;
import com.example.hr_management.repository.DepartmentRepository;
import com.example.hr_management.repository.EmployeeRepository;
import com.example.hr_management.repository.PositionRepository;
import com.example.hr_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Tạo Admin nếu chưa có
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@company.com");
            admin.setRole(User.Role.ADMIN);
            admin.setStatus(User.AccountStatus.ACTIVE);
            userRepository.save(admin);
            log.info("Da tao tai khoan admin mac dinh: username=admin, password=admin123");
        }

        // Tạo dữ liệu mẫu nếu DB hoàn toàn trống (chưa có phòng ban nào)
        if (departmentRepository.count() == 0) {
            log.info("Bat dau tao du lieu mau (Departments, Positions, Employees)...");

            // 1. Tạo phòng ban
            Department devDept = new Department();
            devDept.setCode("PB01");
            devDept.setName("Phòng Kỹ Thuật");
            devDept.setDescription("Phát triển phần mềm");
            devDept.setStatus(Department.DepartmentStatus.ACTIVE);
            departmentRepository.save(devDept);

            Department hrDept = new Department();
            hrDept.setCode("PB02");
            hrDept.setName("Phòng Nhân Sự");
            hrDept.setDescription("Quản lý nhân sự và tuyển dụng");
            hrDept.setStatus(Department.DepartmentStatus.ACTIVE);
            departmentRepository.save(hrDept);

            // 2. Tạo chức vụ
            Position managerPos = new Position();
            managerPos.setCode("CV01");
            managerPos.setName("Trưởng Phòng");
            managerPos.setDescription("Quản lý phòng ban");
            managerPos.setStatus(Position.PositionStatus.ACTIVE);
            positionRepository.save(managerPos);

            Position devPos = new Position();
            devPos.setCode("CV02");
            devPos.setName("Lập Trình Viên");
            devPos.setDescription("Thực hiện viết code");
            devPos.setStatus(Position.PositionStatus.ACTIVE);
            positionRepository.save(devPos);

            Position hrPos = new Position();
            hrPos.setCode("CV03");
            hrPos.setName("Chuyên Viên NS");
            hrPos.setDescription("Tuyển dụng và đào tạo");
            hrPos.setStatus(Position.PositionStatus.ACTIVE);
            positionRepository.save(hrPos);

            // 3. Tạo nhân viên mẫu kèm tài khoản
            createEmployeeAndUser("NV001", "Nguyễn Văn An", "an.nguyen@company.com", "0901234567",
                    Employee.Gender.MALE, LocalDate.of(1990, 1, 15), LocalDate.of(2022, 3, 1),
                    devDept, managerPos, "nv001");

            createEmployeeAndUser("NV002", "Trần Thị Bích", "bich.tran@company.com", "0912345678",
                    Employee.Gender.FEMALE, LocalDate.of(1992, 5, 20), LocalDate.of(2021, 6, 1),
                    devDept, devPos, "nv002");

            createEmployeeAndUser("NV003", "Lê Văn Cường", "cuong.le@company.com", "0923456789",
                    Employee.Gender.MALE, LocalDate.of(1988, 11, 10), LocalDate.of(2020, 1, 15),
                    hrDept, hrPos, "nv003");

            log.info("Tao du lieu mau hoan tat!");
        }
    }

    private void createEmployeeAndUser(String code, String name, String email, String phone,
                                       Employee.Gender gender, LocalDate dob, LocalDate startDate,
                                       Department dept, Position pos, String username) {
        // Tạo nhân viên
        Employee emp = new Employee();
        emp.setCode(code);
        emp.setFullName(name);
        emp.setEmail(email);
        emp.setPhone(phone);
        emp.setGender(gender);
        emp.setDateOfBirth(dob);
        emp.setStartDate(startDate);
        emp.setDepartment(dept);
        emp.setPosition(pos);
        emp.setStatus(Employee.EmployeeStatus.WORKING);
        employeeRepository.save(emp);

        // Tạo tài khoản user cho nhân viên
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setEmail(email);
        user.setRole(User.Role.EMPLOYEE);
        user.setStatus(User.AccountStatus.ACTIVE);
        user.setEmployee(emp);
        userRepository.save(user);
    }
}

