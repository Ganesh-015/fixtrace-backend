package com.fixtrace.config;

import com.fixtrace.entity.*;
import com.fixtrace.enums.*;
import com.fixtrace.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    private final UserRepository userRepository;
    private final LabRepository labRepository;
    private final EquipmentRepository equipmentRepository;
    private final IssueRepository issueRepository;
    private final RepairRecordRepository repairRecordRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            UserRepository userRepository,
            LabRepository labRepository,
            EquipmentRepository equipmentRepository,
            IssueRepository issueRepository,
            RepairRecordRepository repairRecordRepository,
            NotificationRepository notificationRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.labRepository = labRepository;
        this.equipmentRepository = equipmentRepository;
        this.issueRepository = issueRepository;
        this.repairRecordRepository = repairRecordRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("Data seeding is disabled by configuration (app.seed.enabled=false).");
            return;
        }

        if (userRepository.count() > 0) {
            log.info("Database already contains user records. Skipping data seed.");
            return;
        }

        log.info("Starting FixTrace development data seed...");

        // 1. Seed Users (1 Admin, 2 Technicians, 2 Staff, 2 Students)
        User admin = createUser("System Admin", "admin@fixtrace.com", "Admin@123", "+1-555-0100", Role.ADMIN);
        User techAlex = createUser("Alex Vance", "tech.alex@fixtrace.com", "Tech@123", "+1-555-0201", Role.TECHNICIAN);
        User techSarah = createUser("Sarah Connor", "tech.sarah@fixtrace.com", "Tech@123", "+1-555-0202", Role.TECHNICIAN);
        User staffJohn = createUser("Prof. John Miller", "prof.john@university.edu", "Staff@123", "+1-555-0301", Role.STAFF);
        User staffMary = createUser("Mary Higgins (Lab Admin)", "labadmin.mary@university.edu", "Staff@123", "+1-555-0302", Role.STAFF);
        User studentKevin = createUser("Kevin Flynn", "student.kevin@university.edu", "Student@123", "+1-555-0401", Role.STUDENT);
        User studentLisa = createUser("Lisa Simpson", "student.lisa@university.edu", "Student@123", "+1-555-0402", Role.STUDENT);

        // 2. Seed Labs
        Lab labCs = createLab("LAB-CS-101", "Computer Science Main Lab", "Turing Science Center", "1st Floor", "Room 101", "Primary lab for operating systems, programming, and web development courses.");
        Lab labEc = createLab("LAB-EC-204", "Electronics & Circuits Lab", "Maxwell Engineering Hall", "2nd Floor", "Room 204", "Hardware electronics testing, microcontrollers, and circuit prototyping laboratory.");
        Lab labAi = createLab("LAB-AI-305", "Artificial Intelligence & Robotics Lab", "Turing Science Center", "3rd Floor", "Room 305", "Advanced GPU computing, neural network training, and mobile robotics work cell.");

        // 3. Seed Equipment (11 assets)
        Equipment eq1 = createEquipment("EQ-CS-001", "Dell Precision 3660 Workstation", EquipmentCategory.COMPUTER, "Dell", "Precision 3660", "SN-DEL-89471", LocalDate.now().minusMonths(14), LocalDate.now().plusMonths(22), EquipmentStatus.WORKING, labCs, "Core i9, 64GB RAM, RTX 4080 GPU");
        Equipment eq2 = createEquipment("EQ-CS-002", "Dell Precision 3660 Workstation", EquipmentCategory.COMPUTER, "Dell", "Precision 3660", "SN-DEL-89472", LocalDate.now().minusMonths(14), LocalDate.now().plusMonths(22), EquipmentStatus.UNDER_REPAIR, labCs, "Core i9, 64GB RAM, RTX 4080 GPU");
        Equipment eq3 = createEquipment("EQ-CS-003", "Dell UltraSharp 27\" 4K Monitor", EquipmentCategory.MONITOR, "Dell", "U2723QE", "SN-DEL-MON-101", LocalDate.now().minusMonths(10), LocalDate.now().plusMonths(26), EquipmentStatus.WORKING, labCs, "4K IPS Black USB-C Hub Monitor");
        Equipment eq4 = createEquipment("EQ-CS-004", "HP LaserJet Enterprise MFP", EquipmentCategory.PRINTER, "HP", "M528dn", "SN-HP-PRN-552", LocalDate.now().minusMonths(20), LocalDate.now().plusMonths(16), EquipmentStatus.DAMAGED, labCs, "High-capacity network laser printer with duplex scanner");
        Equipment eq5 = createEquipment("EQ-CS-005", "Epson PowerLite 2250U Projector", EquipmentCategory.PROJECTOR, "Epson", "PowerLite 2250U", "SN-EPS-PRJ-301", LocalDate.now().minusMonths(18), LocalDate.now().plusMonths(6), EquipmentStatus.WORKING, labCs, "5,000 lumens Full HD WUXGA Classroom Projector");

        Equipment eq6 = createEquipment("EQ-EC-001", "Tektronix Digital Storage Oscilloscope", EquipmentCategory.ELECTRONIC_EQUIPMENT, "Tektronix", "TBS1052B", "SN-TEK-OSC-401", LocalDate.now().minusMonths(24), LocalDate.now().plusMonths(12), EquipmentStatus.WORKING, labEc, "50 MHz, 2 Channel, 1 GS/s sampling rate");
        Equipment eq7 = createEquipment("EQ-EC-002", "Rigol Arbitrary Function Generator", EquipmentCategory.ELECTRONIC_EQUIPMENT, "Rigol", "DG1022Z", "SN-RIG-GEN-201", LocalDate.now().minusMonths(12), LocalDate.now().plusMonths(24), EquipmentStatus.UNDER_REPAIR, labEc, "25 MHz Function/Arbitrary Waveform Generator");
        Equipment eq8 = createEquipment("EQ-EC-003", "APC Smart-UPS RT 3000VA", EquipmentCategory.UPS, "APC", "SURTD3000XLI", "SN-APC-UPS-771", LocalDate.now().minusMonths(30), LocalDate.now().plusMonths(6), EquipmentStatus.WORKING, labEc, "Online double-conversion rackmount UPS power backup");

        Equipment eq9 = createEquipment("EQ-AI-001", "Lambda Tensorbook Deep Learning Laptop", EquipmentCategory.COMPUTER, "Lambda", "Tensorbook", "SN-LAM-AI-001", LocalDate.now().minusMonths(8), LocalDate.now().plusMonths(28), EquipmentStatus.WORKING, labAi, "Linux workstation configured with PyTorch, TensorFlow, CUDA");
        Equipment eq10 = createEquipment("EQ-AI-002", "Cisco Catalyst 9300 48-Port Switch", EquipmentCategory.NETWORK_DEVICE, "Cisco", "C9300-48T", "SN-CIS-SW-991", LocalDate.now().minusMonths(16), LocalDate.now().plusMonths(20), EquipmentStatus.WORKING, labAi, "Enterprise gigabit network switch with PoE+ support");
        Equipment eq11 = createEquipment("EQ-AI-003", "Sony 4K Laser Projector (Legacy)", EquipmentCategory.PROJECTOR, "Sony", "VPL-FHZ75", "SN-SNY-PRJ-882", LocalDate.now().minusMonths(60), LocalDate.now().minusMonths(12), EquipmentStatus.RETIRED, labAi, "Decommissioned projector replaced by digital whiteboard");

        // 4. Seed Issues
        Issue issue1 = new Issue();
        issue1.setIssueCode("FT-2026-000001");
        issue1.setTitle("Workstation power supply failure");
        issue1.setDescription("Workstation turns off abruptly after 5 minutes of load; burning smell detected near rear exhaust.");
        issue1.setReportedBy(studentKevin);
        issue1.setEquipment(eq2);
        issue1.setLab(labCs);
        issue1.setPriority(IssuePriority.CRITICAL);
        issue1.setStatus(IssueStatus.ASSIGNED);
        issue1.setAssignedTechnician(techAlex);
        issue1.setReportedAt(LocalDateTime.now().minusDays(3));
        issue1.setAssignedAt(LocalDateTime.now().minusDays(2));
        issueRepository.save(issue1);

        Issue issue2 = new Issue();
        issue2.setIssueCode("FT-2026-000002");
        issue2.setTitle("Frequent paper jam on Tray 2");
        issue2.setDescription("Printer reports paper jam even when tray is clear; rollers make repeated grinding sound.");
        issue2.setReportedBy(staffJohn);
        issue2.setEquipment(eq4);
        issue2.setLab(labCs);
        issue2.setPriority(IssuePriority.HIGH);
        issue2.setStatus(IssueStatus.IN_PROGRESS);
        issue2.setAssignedTechnician(techAlex);
        issue2.setReportedAt(LocalDateTime.now().minusDays(4));
        issue2.setAssignedAt(LocalDateTime.now().minusDays(3));
        issueRepository.save(issue2);

        Issue issue3 = new Issue();
        issue3.setIssueCode("FT-2026-000003");
        issue3.setTitle("Distorted waveform output on Channel 2");
        issue3.setDescription("Channel 2 signal amplitude fluctuates randomly and shows 30% offset compared to calibration.");
        issue3.setReportedBy(staffMary);
        issue3.setEquipment(eq7);
        issue3.setLab(labEc);
        issue3.setPriority(IssuePriority.MEDIUM);
        issue3.setStatus(IssueStatus.WAITING_FOR_PARTS);
        issue3.setAssignedTechnician(techSarah);
        issue3.setReportedAt(LocalDateTime.now().minusDays(7));
        issue3.setAssignedAt(LocalDateTime.now().minusDays(6));
        issueRepository.save(issue3);

        Issue issue4 = new Issue();
        issue4.setIssueCode("FT-2026-000004");
        issue4.setTitle("Monitor backlight flickering intermittently");
        issue4.setDescription("Screen flickers off for 2 seconds every 15 minutes when using HDMI input.");
        issue4.setReportedBy(studentLisa);
        issue4.setEquipment(eq3);
        issue4.setLab(labCs);
        issue4.setPriority(IssuePriority.LOW);
        issue4.setStatus(IssueStatus.RESOLVED);
        issue4.setAssignedTechnician(techSarah);
        issue4.setReportedAt(LocalDateTime.now().minusDays(10));
        issue4.setAssignedAt(LocalDateTime.now().minusDays(9));
        issue4.setResolvedAt(LocalDateTime.now().minusDays(8));
        issueRepository.save(issue4);

        Issue issue5 = new Issue();
        issue5.setIssueCode("FT-2026-000005");
        issue5.setTitle("Network port 24 packet drop issue");
        issue5.setDescription("CRC alignment errors recorded on port connecting GPU cluster node 3.");
        issue5.setReportedBy(staffJohn);
        issue5.setEquipment(eq10);
        issue5.setLab(labAi);
        issue5.setPriority(IssuePriority.HIGH);
        issue5.setStatus(IssueStatus.CLOSED);
        issue5.setAssignedTechnician(techAlex);
        issue5.setReportedAt(LocalDateTime.now().minusDays(15));
        issue5.setAssignedAt(LocalDateTime.now().minusDays(14));
        issue5.setResolvedAt(LocalDateTime.now().minusDays(13));
        issueRepository.save(issue5);

        // 5. Seed Repair Records
        RepairRecord rep1 = new RepairRecord();
        rep1.setIssue(issue2);
        rep1.setTechnician(techAlex);
        rep1.setDiagnosis("Feed roller rubber is glazed and worn out; paper sensor optical eye covered in toner dust.");
        rep1.setActionTaken("Cleaned optical sensor with isopropyl alcohol; disassembled tray 2 mechanism.");
        rep1.setPartsUsed("HP Separation Roller Kit RM2-5642");
        rep1.setRepairNotes("Awaiting roller replacement assembly from stockroom.");
        rep1.setRepairStartedAt(LocalDateTime.now().minusDays(3).plusHours(2));
        repairRecordRepository.save(rep1);

        RepairRecord rep2 = new RepairRecord();
        rep2.setIssue(issue3);
        rep2.setTechnician(techSarah);
        rep2.setDiagnosis("Burnt surface-mount decoupling capacitor on analog front-end amplifier stage.");
        rep2.setActionTaken("Removed fried 10uF 0805 SMD capacitor and verified PCB trace continuity.");
        rep2.setPartsUsed("10uF 50V X7R SMD capacitor (ordered)");
        rep2.setRepairNotes("Part out of local stock; requisition order #REQ-8839 placed with supplier.");
        rep2.setRepairStartedAt(LocalDateTime.now().minusDays(6).plusHours(1));
        repairRecordRepository.save(rep2);

        RepairRecord rep3 = new RepairRecord();
        rep3.setIssue(issue4);
        rep3.setTechnician(techSarah);
        rep3.setDiagnosis("Cracked solder joint on HDMI port pin 19 (Hot Plug Detect).");
        rep3.setActionTaken("Re-soldered connector pins under stereo microscope and re-flowed solder.");
        rep3.setPartsUsed("Lead-free solder wire 0.5mm");
        rep3.setRepairNotes("Stress-tested at 4K 60Hz resolution for 2 hours with zero signal drop.");
        rep3.setRepairStartedAt(LocalDateTime.now().minusDays(9).plusHours(3));
        rep3.setRepairCompletedAt(LocalDateTime.now().minusDays(8));
        repairRecordRepository.save(rep3);

        RepairRecord rep4 = new RepairRecord();
        rep4.setIssue(issue5);
        rep4.setTechnician(techAlex);
        rep4.setDiagnosis("Damaged RJ-45 copper jack contacts due to excessive cable pull.");
        rep4.setActionTaken("Replaced Cat6 patch cable and cleared interface counters; port operating cleanly at 1 Gbps full-duplex.");
        rep4.setPartsUsed("Cat6 SFTP patch lead 3m");
        rep4.setRepairNotes("All CRC error counters reset to 0; no further drops observed.");
        rep4.setRepairStartedAt(LocalDateTime.now().minusDays(14));
        rep4.setRepairCompletedAt(LocalDateTime.now().minusDays(13));
        repairRecordRepository.save(rep4);

        // 6. Seed Notifications
        createNotification(techAlex, "New Issue Assigned: FT-2026-000001", "You have been assigned to repair equipment EQ-CS-002", NotificationType.ISSUE_ASSIGNED);
        createNotification(studentKevin, "Issue Status Update: FT-2026-000001", "Your issue has been assigned to technician Alex Vance", NotificationType.ISSUE_STATUS_CHANGED);
        createNotification(studentLisa, "Issue Resolved: FT-2026-000004", "Your monitor flickering issue has been resolved by Sarah Connor", NotificationType.ISSUE_RESOLVED);
        createNotification(staffJohn, "Issue Closed: FT-2026-000005", "Network port 24 packet drop issue marked as CLOSED", NotificationType.ISSUE_STATUS_CHANGED);

        log.info("FixTrace development data seed completed successfully!");
    }

    private User createUser(String name, String email, String password, String phone, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole(role);
        user.setActive(true);
        return userRepository.save(user);
    }

    private Lab createLab(String code, String name, String building, String floor, String room, String desc) {
        Lab lab = new Lab();
        lab.setLabCode(code);
        lab.setName(name);
        lab.setBuilding(building);
        lab.setFloor(floor);
        lab.setRoomNumber(room);
        lab.setDescription(desc);
        lab.setActive(true);
        return labRepository.save(lab);
    }

    private Equipment createEquipment(String code, String name, EquipmentCategory category, String brand,
                                      String model, String serial, LocalDate purchase, LocalDate warranty,
                                      EquipmentStatus status, Lab lab, String desc) {
        Equipment equipment = new Equipment();
        equipment.setAssetCode(code);
        equipment.setName(name);
        equipment.setCategory(category);
        equipment.setBrand(brand);
        equipment.setModel(model);
        equipment.setSerialNumber(serial);
        equipment.setPurchaseDate(purchase);
        equipment.setWarrantyExpiryDate(warranty);
        equipment.setStatus(status);
        equipment.setLab(lab);
        equipment.setDescription(desc);
        return equipmentRepository.save(equipment);
    }

    private void createNotification(User user, String title, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notificationRepository.save(notification);
    }
}
