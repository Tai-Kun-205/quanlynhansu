-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th10 12, 2026 lúc 10:15 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `hr_management`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `activity_logs`
--

CREATE TABLE `activity_logs` (
  `id` bigint(20) NOT NULL,
  `action` varchar(50) NOT NULL,
  `actor_username` varchar(100) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `module` varchar(50) NOT NULL,
  `target_id` bigint(20) DEFAULT NULL,
  `target_type` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `activity_logs`
--

INSERT INTO `activity_logs` (`id`, `action`, `actor_username`, `created_at`, `description`, `ip_address`, `module`, `target_id`, `target_type`) VALUES
(1, 'LOCK', 'admin', '2026-10-03 16:16:29.000000', 'Khóa tài khoản nv001', NULL, 'TÀI KHOẢN', 2, 'User'),
(2, 'UNLOCK', 'admin', '2026-10-03 16:16:29.000000', 'Mở khóa tài khoản nv001', NULL, 'TÀI KHOẢN', 2, 'User');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `attendances`
--

CREATE TABLE `attendances` (
  `id` bigint(20) NOT NULL,
  `check_in_time` time DEFAULT NULL,
  `check_out_time` time DEFAULT NULL,
  `status` enum('ABSENT','EARLY_LEAVE','LATE','NOT_CHECKED','PRESENT') NOT NULL,
  `work_date` date NOT NULL,
  `work_hours` double DEFAULT NULL,
  `employee_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `attendances`
--

INSERT INTO `attendances` (`id`, `check_in_time`, `check_out_time`, `status`, `work_date`, `work_hours`, `employee_id`) VALUES
(1, '20:28:47', '20:28:56', 'LATE', '2026-10-01', 0, 1),
(2, '10:23:58', '10:23:59', 'EARLY_LEAVE', '2026-10-03', 0, 1),
(3, '15:31:13', '15:31:17', 'EARLY_LEAVE', '2026-10-03', 0, 2),
(4, '23:07:58', '23:07:58', 'LATE', '2026-10-05', 0, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `departments`
--

CREATE TABLE `departments` (
  `id` bigint(20) NOT NULL,
  `code` varchar(50) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `departments`
--

INSERT INTO `departments` (`id`, `code`, `description`, `name`, `status`) VALUES
(1, 'PB01', 'Quan ly nhan su', 'Phong Nhan Su', 'ACTIVE'),
(2, 'PB02', 'Phat trien phan mem', 'Phong Ky Thuat', 'ACTIVE'),
(9, 'PB03', 'Phụ trách kinh doanh và phát triển thị trường', 'Phòng Kinh Doanh', 'ACTIVE'),
(10, 'PB04', 'Phụ trách tài chính và kế toán', 'Phòng Kế Toán', 'ACTIVE'),
(11, 'PB05', 'Phụ trách truyền thông và marketing', 'Phòng Marketing', 'ACTIVE'),
(12, 'PB06', 'Phụ trách hành chính và văn phòng', 'Phòng Hành Chính', 'ACTIVE'),
(13, 'PB07', 'Phụ trách hỗ trợ và chăm sóc khách hàng', 'Phòng Chăm Sóc Khách Hàng', 'ACTIVE'),
(14, 'PB08', 'Phụ trách kiểm tra chất lượng', 'Phòng Kiểm Soát Chất Lượng', 'ACTIVE'),
(15, 'PB10', 'Phụ trách nghiên cứu và phát triển sản phẩm', 'Phòng Nghiên Cứu Phát Triển', 'ACTIVE');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `employees`
--

CREATE TABLE `employees` (
  `id` bigint(20) NOT NULL,
  `address` varchar(500) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `date_of_birth` date DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `full_name` varchar(100) NOT NULL,
  `gender` enum('FEMALE','MALE','OTHER') DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `status` enum('ON_LEAVE','RESIGNED','WORKING') NOT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `position_id` bigint(20) DEFAULT NULL,
  `avatar_url` varchar(500) DEFAULT NULL,
  `education_level` varchar(100) DEFAULT NULL,
  `emergency_contact_name` varchar(100) DEFAULT NULL,
  `emergency_contact_phone` varchar(20) DEFAULT NULL,
  `emergency_contact_relationship` varchar(50) DEFAULT NULL,
  `major` varchar(150) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `employees`
--

INSERT INTO `employees` (`id`, `address`, `code`, `date_of_birth`, `email`, `full_name`, `gender`, `phone`, `start_date`, `status`, `department_id`, `position_id`, `avatar_url`, `education_level`, `emergency_contact_name`, `emergency_contact_phone`, `emergency_contact_relationship`, `major`) VALUES
(1, NULL, 'NV001', '1990-01-15', 'an.nguyen@company.com', 'Nguyen Van An', 'MALE', '0901234567', '2022-03-01', 'WORKING', 1, 1, NULL, NULL, NULL, NULL, NULL, NULL),
(2, NULL, 'NV002', '1992-05-20', 'bich.tran@company.com', 'Tran Thi Bich', 'FEMALE', '0912345678', '2021-06-01', 'WORKING', 1, 2, NULL, NULL, NULL, NULL, NULL, NULL),
(3, NULL, 'NV003', '1988-11-10', 'cuong.le@company.com', 'Le Van Cuong', 'MALE', '0923456789', '2020-01-15', 'WORKING', 2, 1, NULL, NULL, NULL, NULL, NULL, NULL),
(4, NULL, 'NV004', '1995-03-08', 'dung.pham@company.com', 'Pham Thi Dung', 'FEMALE', '0934567890', '2023-03-01', 'WORKING', 2, 3, NULL, NULL, NULL, NULL, NULL, NULL),
(5, NULL, 'NV005', '1985-07-22', 'em.hoang@company.com', 'Hoang Van Em', 'MALE', '0945678901', '2019-09-01', 'WORKING', 1, 2, NULL, NULL, NULL, NULL, NULL, NULL),
(6, NULL, 'NV006', '1996-12-02', 'phuong.vu@company.com', 'Vũ Thị Phương', 'FEMALE', '0956789012', '2024-01-10', 'WORKING', 2, 3, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `leave_requests`
--

CREATE TABLE `leave_requests` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `end_date` date NOT NULL,
  `reason` varchar(1000) NOT NULL,
  `start_date` date NOT NULL,
  `status` enum('APPROVED','CANCELLED','PENDING','REJECTED') NOT NULL,
  `employee_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `leave_requests`
--

INSERT INTO `leave_requests` (`id`, `created_at`, `end_date`, `reason`, `start_date`, `status`, `employee_id`) VALUES
(1, '2026-10-01 20:28:47.000000', '2026-10-06', 'Nghi phep ca nhan', '2026-10-05', 'APPROVED', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `notifications`
--

CREATE TABLE `notifications` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `content` text NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(100) NOT NULL,
  `title` varchar(200) NOT NULL,
  `department_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `notifications`
--

INSERT INTO `notifications` (`id`, `active`, `content`, `created_at`, `created_by`, `title`, `department_id`) VALUES
(1, b'1', 'Noi dung demo', '2026-10-03 15:45:50.000000', 'admin', 'Thong bao demo', NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `notification_reads`
--

CREATE TABLE `notification_reads` (
  `id` bigint(20) NOT NULL,
  `read_at` datetime(6) NOT NULL,
  `notification_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `notification_reads`
--

INSERT INTO `notification_reads` (`id`, `read_at`, `notification_id`, `user_id`) VALUES
(1, '2026-10-03 17:00:02.000000', 1, 2);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `payrolls`
--

CREATE TABLE `payrolls` (
  `id` bigint(20) NOT NULL,
  `actual_work_days` int(11) NOT NULL,
  `allowance` decimal(15,2) DEFAULT NULL,
  `base_salary` decimal(15,2) NOT NULL,
  `bonus` decimal(15,2) DEFAULT NULL,
  `deduction` decimal(15,2) DEFAULT NULL,
  `payroll_month` int(11) NOT NULL,
  `net_salary` decimal(15,2) NOT NULL,
  `standard_work_days` int(11) NOT NULL,
  `status` enum('DRAFT','FINALIZED') NOT NULL,
  `payroll_year` int(11) NOT NULL,
  `employee_id` bigint(20) NOT NULL,
  `absent_days` int(11) NOT NULL,
  `attendance_salary` decimal(15,2) NOT NULL,
  `early_leave_days` int(11) NOT NULL,
  `late_days` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `payrolls`
--

INSERT INTO `payrolls` (`id`, `actual_work_days`, `allowance`, `base_salary`, `bonus`, `deduction`, `payroll_month`, `net_salary`, `standard_work_days`, `status`, `payroll_year`, `employee_id`, `absent_days`, `attendance_salary`, `early_leave_days`, `late_days`) VALUES
(1, 1, 1000000.00, 10000000.00, 500000.00, 200000.00, 10, 1684615.38, 26, 'FINALIZED', 2026, 2, 0, 384615.38, 1, 0),
(2, 3, 500000.00, 10400000.00, 200000.00, 100000.00, 10, 1800000.00, 26, 'DRAFT', 2026, 1, 0, 1200000.00, 1, 2);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `positions`
--

CREATE TABLE `positions` (
  `id` bigint(20) NOT NULL,
  `code` varchar(50) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `positions`
--

INSERT INTO `positions` (`id`, `code`, `description`, `name`, `status`) VALUES
(1, 'CV01', 'Quan ly phong', 'Truong phong', 'ACTIVE'),
(2, 'CV02', 'Thuc hien cong viec', 'Nhan vien', 'ACTIVE'),
(3, 'CV03', 'Lap trinh vien', 'Ky su phan mem', 'ACTIVE');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','EMPLOYEE') NOT NULL,
  `status` enum('ACTIVE','LOCKED') NOT NULL,
  `username` varchar(50) NOT NULL,
  `employee_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `email`, `password`, `role`, `status`, `username`, `employee_id`) VALUES
(1, 'admin@company.com', '$2a$10$1852QF5nwVUsK6rI/jzBZO1KZc.9V4J1GrIh9Q8CDlfeIjTpwgSTa', 'ADMIN', 'ACTIVE', 'admin', NULL),
(2, 'an.nguyen@company.com', '$2a$10$1852QF5nwVUsK6rI/jzBZO1KZc.9V4J1GrIh9Q8CDlfeIjTpwgSTa', 'EMPLOYEE', 'ACTIVE', 'nv001', 1),
(3, 'bich.tran@company.com', '$2a$10$YHZKpGfUrDHUQTsFiqOyQ.4.JBkFQ8lY2Q/o8cq8tn5QvxqSuDIuO', 'EMPLOYEE', 'ACTIVE', 'nv002', 2),
(4, 'cuong.le@company.com', '$2a$10$dvbRWp3TsgP6mAbEHqsU8uckQGG6XDZQeC7ukNhw6mcIyipxYVpv2', 'EMPLOYEE', 'ACTIVE', 'nv003', 3),
(5, 'dung.pham@company.com', '$2a$10$Uipm1Q8aDuJeVEDp7HQYieKG9SngV2ooW595iCgWtygHSSyO093cm', 'EMPLOYEE', 'ACTIVE', 'nv004', 4),
(6, 'phuong.vu@company.com', '$2a$10$7SBdbbMjEaUv6Nc/DO6qbuIQptMdrHiOH8uIUZbfm948eYmot0G6S', 'EMPLOYEE', 'ACTIVE', 'nv006', 6);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `work_assignments`
--

CREATE TABLE `work_assignments` (
  `id` bigint(20) NOT NULL,
  `note` varchar(500) DEFAULT NULL,
  `work_date` date NOT NULL,
  `employee_id` bigint(20) NOT NULL,
  `shift_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `work_assignments`
--

INSERT INTO `work_assignments` (`id`, `note`, `work_date`, `employee_id`, `shift_id`) VALUES
(1, 'Demo', '2026-10-04', 2, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `work_shifts`
--

CREATE TABLE `work_shifts` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `code` varchar(30) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `end_time` time NOT NULL,
  `name` varchar(100) NOT NULL,
  `start_time` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `work_shifts`
--

INSERT INTO `work_shifts` (`id`, `active`, `code`, `description`, `end_time`, `name`, `start_time`) VALUES
(1, b'1', 'HC', 'Ca tieu chuan', '17:00:00', 'Hanh chinh', '08:00:00');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `activity_logs`
--
ALTER TABLE `activity_logs`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `attendances`
--
ALTER TABLE `attendances`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK2mia0pcnmy2nddwedvfrt0w08` (`employee_id`);

--
-- Chỉ mục cho bảng `departments`
--
ALTER TABLE `departments`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKl7tivi5261wxdnvo6cct9gg6t` (`code`),
  ADD UNIQUE KEY `UKj6cwks7xecs5jov19ro8ge3qk` (`name`);

--
-- Chỉ mục cho bảng `employees`
--
ALTER TABLE `employees`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK3um79qgwg340lpaw7phtwudtc` (`code`),
  ADD UNIQUE KEY `UKj9xgmd0ya5jmus09o0b8pqrpb` (`email`),
  ADD KEY `FKgy4qe3dnqrm3ktd76sxp7n4c2` (`department_id`),
  ADD KEY `FKngcpgx7fx5kednw3m7u0u8of3` (`position_id`);

--
-- Chỉ mục cho bảng `leave_requests`
--
ALTER TABLE `leave_requests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKrxff2xg1kffbjfh5maxwoqyhw` (`employee_id`);

--
-- Chỉ mục cho bảng `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK2o79q2wo75bo7fttqlvl9htg0` (`department_id`);

--
-- Chỉ mục cho bảng `notification_reads`
--
ALTER TABLE `notification_reads`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKmovgbjijrkn1ommxqwv16h5pp` (`notification_id`,`user_id`),
  ADD KEY `FKnlh7pma2y4w8vti9wu17ynqk` (`user_id`);

--
-- Chỉ mục cho bảng `payrolls`
--
ALTER TABLE `payrolls`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKqki5p5yyx3ms1r6mxfnrt5eqt` (`employee_id`,`payroll_month`,`payroll_year`);

--
-- Chỉ mục cho bảng `positions`
--
ALTER TABLE `positions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKdjkia0ifarv9epmv78bh62r3o` (`code`),
  ADD UNIQUE KEY `UK3vhyopdpf9huqh1t67ho6nayj` (`name`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  ADD UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  ADD UNIQUE KEY `UKd1s31g1a7ilra77m65xmka3ei` (`employee_id`);

--
-- Chỉ mục cho bảng `work_assignments`
--
ALTER TABLE `work_assignments`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKfjmjmsodl1fdihscm9xsqsvyr` (`employee_id`,`work_date`),
  ADD KEY `FKjebpsec24v73fvgr39of4d1uq` (`shift_id`);

--
-- Chỉ mục cho bảng `work_shifts`
--
ALTER TABLE `work_shifts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK4nb0vccy88t24m85dlapoa04j` (`code`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `activity_logs`
--
ALTER TABLE `activity_logs`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `attendances`
--
ALTER TABLE `attendances`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `departments`
--
ALTER TABLE `departments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT cho bảng `employees`
--
ALTER TABLE `employees`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT cho bảng `leave_requests`
--
ALTER TABLE `leave_requests`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT cho bảng `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT cho bảng `notification_reads`
--
ALTER TABLE `notification_reads`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT cho bảng `payrolls`
--
ALTER TABLE `payrolls`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `positions`
--
ALTER TABLE `positions`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT cho bảng `work_assignments`
--
ALTER TABLE `work_assignments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT cho bảng `work_shifts`
--
ALTER TABLE `work_shifts`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `attendances`
--
ALTER TABLE `attendances`
  ADD CONSTRAINT `FK2mia0pcnmy2nddwedvfrt0w08` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`);

--
-- Các ràng buộc cho bảng `employees`
--
ALTER TABLE `employees`
  ADD CONSTRAINT `FKgy4qe3dnqrm3ktd76sxp7n4c2` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`),
  ADD CONSTRAINT `FKngcpgx7fx5kednw3m7u0u8of3` FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`);

--
-- Các ràng buộc cho bảng `leave_requests`
--
ALTER TABLE `leave_requests`
  ADD CONSTRAINT `FKrxff2xg1kffbjfh5maxwoqyhw` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`);

--
-- Các ràng buộc cho bảng `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `FK2o79q2wo75bo7fttqlvl9htg0` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`);

--
-- Các ràng buộc cho bảng `notification_reads`
--
ALTER TABLE `notification_reads`
  ADD CONSTRAINT `FKg4vrpg3nw9pe3fdskpba65rjp` FOREIGN KEY (`notification_id`) REFERENCES `notifications` (`id`),
  ADD CONSTRAINT `FKnlh7pma2y4w8vti9wu17ynqk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `payrolls`
--
ALTER TABLE `payrolls`
  ADD CONSTRAINT `FKiyfp8uysuhgfkod3xcdhjm7qf` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`);

--
-- Các ràng buộc cho bảng `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `FK6p2ib82uai0pj9yk1iassppgq` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`);

--
-- Các ràng buộc cho bảng `work_assignments`
--
ALTER TABLE `work_assignments`
  ADD CONSTRAINT `FKjebpsec24v73fvgr39of4d1uq` FOREIGN KEY (`shift_id`) REFERENCES `work_shifts` (`id`),
  ADD CONSTRAINT `FKnqxu7qic4cebf6yxlmwg4ybm8` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
