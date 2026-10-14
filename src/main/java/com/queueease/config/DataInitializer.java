package com.queueease.config;

import com.queueease.entity.*;
import com.queueease.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;
    private final QueueEntryRepository queueEntryRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            BusinessRepository businessRepository,
            ServiceRepository serviceRepository,
            QueueEntryRepository queueEntryRepository,
            BookingRepository bookingRepository,
            ReviewRepository reviewRepository,
            StatusHistoryRepository statusHistoryRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.serviceRepository = serviceRepository;
        this.queueEntryRepository = queueEntryRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // If already seeded with diverse business categories, skip
        if (businessRepository.count() >= 6 && !businessRepository.findByCategoryIgnoreCaseAndActiveTrue("Clinics & Health").isEmpty()) {
            return;
        }

        // Clean prior sample data to reseed comprehensive multi-business ecosystem
        statusHistoryRepository.deleteAllInBatch();
        queueEntryRepository.deleteAllInBatch();
        bookingRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        serviceRepository.deleteAllInBatch();
        businessRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        String commonPassword = passwordEncoder.encode("password123");

        // 1. Users
        User admin = createUser("System Admin", "admin@queueease.com", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN, "+1 555-0100");
        User cust1 = createUser("Sarah Jenkins", "sarah@example.com", passwordEncoder.encode("customer123"), Role.ROLE_CUSTOMER, "+1 555-0101");
        User cust2 = createUser("Liam Chen", "liam@example.com", commonPassword, Role.ROLE_CUSTOMER, "+1 555-0102");
        User cust3 = createUser("Elena Rostova", "elena@example.com", commonPassword, Role.ROLE_CUSTOMER, "+1 555-0103");
        User cust4 = createUser("Marcus Vance", "marcus@example.com", commonPassword, Role.ROLE_CUSTOMER, "+1 555-0104");
        User cust5 = createUser("Amara Okafor", "amara@example.com", commonPassword, Role.ROLE_CUSTOMER, "+1 555-0105");

        // Staff for each business category
        User staffClinic = createUser("Dr. Maya Lin", "clinic_staff@queueease.com", passwordEncoder.encode("staff123"), Role.ROLE_STAFF, "+1 555-0201");
        User staffPlay = createUser("Leo Brooks", "play_staff@queueease.com", passwordEncoder.encode("staff123"), Role.ROLE_STAFF, "+1 555-0202");
        User staffSalon = createUser("Alex Vance", "salon_staff@queueease.com", passwordEncoder.encode("staff123"), Role.ROLE_STAFF, "+1 555-0203");
        User staffAuto = createUser("Marcus Miller", "auto_staff@queueease.com", passwordEncoder.encode("staff123"), Role.ROLE_STAFF, "+1 555-0204");
        User staffTech = createUser("Chloe Bennett", "tech_staff@queueease.com", passwordEncoder.encode("staff123"), Role.ROLE_STAFF, "+1 555-0205");
        User staffPet = createUser("Emma Watson", "pet_staff@queueease.com", passwordEncoder.encode("staff123"), Role.ROLE_STAFF, "+1 555-0206");

        // 2. Diverse Small Businesses Across Multiple Categories

        // Category 1: Clinics & Health
        Business b1 = createBusiness(staffClinic, "Apex Family Care & Dental Clinic", "Clinics & Health",
                "Compassionate neighborhood outpatient clinic offering general practitioner checkups, pediatric consultations, ultrasonic dental cleanings, and walk-in urgent care.",
                "782 Medical Center Drive, Suite 101", "Northside", "+1 555-4001", 25, 60);
        ServiceEntity s1_1 = createService(b1, "General Physician Consultation", "Comprehensive health check, symptom diagnosis, vitals review, and prescription.", 20, "50.00");
        ServiceEntity s1_2 = createService(b1, "Ultrasonic Dental Cleaning & Exam", "Plaque removal, tartar scaling, enamel polish, and preventative gum inspection.", 40, "85.00");
        ServiceEntity s1_3 = createService(b1, "Pediatric Child Wellness Visit", "Routine developmental milestones check, physical examination, and parental consultation.", 25, "60.00");
        ServiceEntity s1_4 = createService(b1, "Seasonal Flu Vaccination & Vitals", "Quick walk-in immunization shot and blood pressure/oxygen check.", 15, "30.00");

        // Category 2: Play Areas & Kids
        Business b2 = createBusiness(staffPlay, "JumpZone Kids Play Park & Adventure Arena", "Play Areas & Kids",
                "Premier indoor family amusement center with wall-to-wall trampolines, foam pits, multi-tier soft play jungle gyms, toddler sensory zones, and laser tag.",
                "350 Funland Boulevard", "Eastside Metro", "+1 555-4002", 45, 80);
        ServiceEntity s2_1 = createService(b2, "60-Min Open Trampoline & Soft Play Pass", "Full access to free-jump arenas, foam diving pits, and multi-level climbing structures.", 60, "18.00");
        ServiceEntity s2_2 = createService(b2, "Toddler Sensory Zone (Ages 0-4)", "Dedicated gentle padded soft play area with sensory toys, mini slides, and ball pool.", 45, "12.00");
        ServiceEntity s2_3 = createService(b2, "Laser Tag Mission (Single Match)", "Exciting 15-minute tactical laser tag battle in our multi-level themed neon arena.", 20, "10.00");
        ServiceEntity s2_4 = createService(b2, "All-Access 2-Hour Play & Arcade Combo", "Two hours of boundless trampoline and playground fun plus 30 arcade game credits.", 120, "28.00");

        // Category 3: Salons & Grooming
        Business b3 = createBusiness(staffSalon, "Luxe Hair & Beauty Lounge", "Salons & Grooming",
                "Upscale salon offering personalized haircutting, scalp therapies, dimensional balayage, and executive beard grooming.",
                "104 Walnut Street, Suite A", "Downtown", "+1 555-4003", 30, 35);
        ServiceEntity s3_1 = createService(b3, "Precision Haircut & Blowdry", "Custom cut tailored to face shape, shampoo scalp massage, and professional blowout.", 45, "45.00");
        ServiceEntity s3_2 = createService(b3, "Classic Fade & Razor Lineup", "Crisp skin taper fade, hot neck lather, and straight razor line definition.", 30, "30.00");
        ServiceEntity s3_3 = createService(b3, "Balayage & Conditioning Gloss", "Artisanal hand-painted hair coloring with restorative keratin gloss.", 90, "125.00");
        ServiceEntity s3_4 = createService(b3, "Express Dry Haircut", "Efficient, precise cut for clients with tight schedules.", 20, "25.00");

        // Category 4: Auto & Repairs
        Business b4 = createBusiness(staffAuto, "Precision Auto Care & Quick Lube", "Auto & Repairs",
                "Reliable automotive repair center with certified mechanics handling express oil changes, brake service, tire alignment, and computer diagnostics.",
                "450 Industrial Parkway", "Westside", "+1 555-4004", 35, 30);
        ServiceEntity s4_1 = createService(b4, "Full Synthetic Oil & Filter Service", "Up to 5 quarts premium synthetic motor oil, OEM filter replacement, and 20-point fluid inspection.", 30, "65.00");
        ServiceEntity s4_2 = createService(b4, "Brake System Inspection & Pad Service", "Front/rear brake pad thickness check, rotor resurfacing evaluation, and brake fluid test.", 45, "130.00");
        ServiceEntity s4_3 = createService(b4, "OBD-II Computerized Diagnostic Scan", "Engine fault code reading, battery health report, and electrical sensor check.", 20, "40.00");

        // Category 5: Tech & Device Repair
        Business b5 = createBusiness(staffTech, "QuickFix Express Phone & Laptop Repair", "Auto & Repairs",
                "Fast-turnaround gadget repair lab. Walk-in smartphone screen replacements, battery testing, charging port repairs, and laptop cooling service.",
                "210 Tech Plaza, Unit 4", "Downtown", "+1 555-4005", 25, 30);
        ServiceEntity s5_1 = createService(b5, "Smartphone Screen Replacement", "Premium OLED/LCD glass display assembly replacement and touch calibration.", 30, "79.00");
        ServiceEntity s5_2 = createService(b5, "Battery Diagnostics & Replacement", "Certified OEM-grade lithium battery swap with safe charge cycling test.", 25, "49.00");
        ServiceEntity s5_3 = createService(b5, "Charging Port & Speaker Debris Cleaning", "Ultrasonic port debris extraction and microphone/speaker grille tuneup.", 15, "25.00");

        // Category 6: Pet Care & Grooming
        Business b6 = createBusiness(staffPet, "Happy Paws Pet Spa & Veterinary Clinic", "Pet Care",
                "Holistic pet wellness facility providing gentle hydro-massage baths, scissor coat styling, de-shedding, nail filing, and routine vet health visits.",
                "92 Greenfield Lane", "Uptown", "+1 555-4006", 40, 25);
        ServiceEntity s6_1 = createService(b6, "Full Dog Bath, Groom & De-Shed", "Hypoallergenic shampoo hydro-bath, blueberry facial, blow-dry, brushout, and scissor trim.", 60, "55.00");
        ServiceEntity s6_2 = createService(b6, "Puppy Gentle Wash & Ear Cleaning", "Mild tearless foam bath, brush, ear hygiene cleanup, and paw pad balm.", 35, "35.00");
        ServiceEntity s6_3 = createService(b6, "Routine Vet Wellness & Vaccine Review", "Physical exam, weight check, dental review, heartworm screening, and deworming.", 20, "45.00");
        ServiceEntity s6_4 = createService(b6, "Pedicure & Paw Balm Treatment", "Quick claw trim, smooth rotary filing, and soothing organic shea butter paw balm.", 15, "18.00");

        // 3. Realistic Initial Queue Entries
        // Clinic: Sarah is called to Examination Room 1, Liam is waiting
        createQueueEntry(b1, cust1, s1_1, "C101", QueueStatus.CALLED, 1, 0);
        createQueueEntry(b1, cust2, s1_2, "C102", QueueStatus.WAITING, null, 25);

        // Play Area: Elena has kids waiting for Trampoline Play Pass
        createQueueEntry(b2, cust3, s2_1, "J201", QueueStatus.WAITING, null, 20);

        // Salon: Marcus in Chair 1, Amara waiting
        createQueueEntry(b3, cust4, s3_1, "S301", QueueStatus.IN_SERVICE, 1, 0);
        createQueueEntry(b3, cust5, s3_2, "S302", QueueStatus.WAITING, null, 30);

        // Auto: Tech servicing a car
        createQueueEntry(b4, cust2, s4_1, "A401", QueueStatus.IN_SERVICE, 2, 0);

        // Pet: Dog waiting for full grooming
        createQueueEntry(b6, cust1, s6_1, "P601", QueueStatus.WAITING, null, 40);

        // 4. Sample Bookings
        createBooking(b1, cust4, s1_2, LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(10, 40));
        createBooking(b2, cust5, s2_4, LocalDate.now().plusDays(1), LocalTime.of(14, 0), LocalTime.of(16, 0));
        createBooking(b3, cust1, s3_1, LocalDate.now().plusDays(2), LocalTime.of(11, 0), LocalTime.of(11, 45));

        // 5. Sample Reviews
        createReview(b1, cust1, 5, "Dr. Maya Lin was thorough and gentle. The live queue ticket notified me exactly when to step into room 1!");
        createReview(b2, cust3, 5, "The kids had a blast on the trampolines! Checking the queue before driving over saved us from a long wait.");
        createReview(b3, cust4, 5, "Alex gave me the best fade in the city. Clean, professional, and right on schedule.");
        createReview(b4, cust2, 5, "Fastest synthetic oil change in town. Loved tracking the service progress on my phone.");
        createReview(b6, cust1, 5, "Our golden retriever looked handsome and smelled fantastic after the de-shedding spa!");
    }

    private User createUser(String name, String email, String password, Role role, String phone) {
        return userRepository.findByEmail(email).map(existing -> {
            existing.setName(name);
            existing.setPassword(password);
            existing.setRole(role);
            existing.setPhone(phone);
            existing.setActive(true);
            return userRepository.save(existing);
        }).orElseGet(() -> {
            User u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setPassword(password);
            u.setRole(role);
            u.setPhone(phone);
            u.setActive(true);
            return userRepository.save(u);
        });
    }

    private Business createBusiness(User owner, String name, String category, String description,
                                     String address, String city, String phone, int avgDuration, int capacity) {
        Business b = new Business();
        b.setOwner(owner);
        b.setName(name);
        b.setCategory(category);
        b.setDescription(description);
        b.setAddress(address);
        b.setCity(city);
        b.setPhone(phone);
        b.setOpeningHours("Mon-Sat 08:30 AM - 07:30 PM");
        b.setOpenTime(LocalTime.of(8, 30));
        b.setCloseTime(LocalTime.of(19, 30));
        b.setAvgServiceDurationMinutes(avgDuration);
        b.setDailyCapacity(capacity);
        b.setActive(true);
        b.setQueueOpen(true);
        return businessRepository.save(b);
    }

    private ServiceEntity createService(Business b, String name, String desc, int duration, String price) {
        ServiceEntity s = new ServiceEntity();
        s.setBusiness(b);
        s.setName(name);
        s.setDescription(desc);
        s.setDurationMinutes(duration);
        s.setPrice(new BigDecimal(price));
        s.setActive(true);
        return serviceRepository.save(s);
    }

    private void createQueueEntry(Business b, User c, ServiceEntity s, String ticketNum, QueueStatus status, Integer counter, int wait) {
        QueueEntry q = new QueueEntry();
        q.setBusiness(b);
        q.setCustomer(c);
        q.setCustomerName(c.getName());
        q.setCustomerPhone(c.getPhone());
        q.setService(s);
        q.setTicketNumber(ticketNum);
        q.setStatus(status);
        q.setCounterNumber(counter);
        q.setEstimatedWaitMinutes(wait);
        queueEntryRepository.save(q);
    }

    private void createBooking(Business b, User c, ServiceEntity s, LocalDate date, LocalTime start, LocalTime end) {
        Booking bk = new Booking();
        bk.setBusiness(b);
        bk.setCustomer(c);
        bk.setService(s);
        bk.setBookingDate(date);
        bk.setStartTime(start);
        bk.setEndTime(end);
        bk.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(bk);
    }

    private void createReview(Business b, User c, int rating, String comment) {
        Review r = new Review();
        r.setBusiness(b);
        r.setCustomer(c);
        r.setRating(rating);
        r.setComment(comment);
        reviewRepository.save(r);
    }
}