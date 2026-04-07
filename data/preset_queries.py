# All 14 preset queries aligned to the updated UniversityAccommodation schema.
# Table names: Advisor, Room, Apartment, Apartment_Room, Next_Of_Kin
# Queries with runtime parameters use %s placeholders (mysql-connector style).

PRESET_QUERIES = [
    {
        "id": 1,
        "label": "(a) Hall manager contacts",
        "description": "Manager name and telephone number for each hall of residence",
        "params": [],
        "sql": """
            SELECT
                h.name                                        AS hall_name,
                CONCAT(st.first_name, ' ', st.last_name)      AS manager_name,
                h.phone                                       AS telephone
            FROM Hall h
            JOIN Staff st ON h.manager_id = st.staff_id
            ORDER BY h.name
        """
    },
    {
        "id": 2,
        "label": "(b) Students with lease details",
        "description": "Names and banner numbers of students with their lease agreements",
        "params": [],
        "sql": """
            SELECT
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                l.lease_no,
                l.duration,
                l.place_no,
                l.room_no,
                l.start_date,
                l.end_date
            FROM Student s
            JOIN Lease l ON s.banner_no = l.banner_no
            ORDER BY s.last_name
        """
    },
    {
        "id": 3,
        "label": "(c) Summer semester leases",
        "description": "Details of lease agreements that cover the summer semester (Apr-Jul start)",
        "params": [],
        "sql": """
            SELECT
                l.lease_no,
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                l.duration,
                l.start_date,
                l.end_date,
                l.place_no,
                l.room_no,
                l.address
            FROM Lease l
            JOIN Student s ON l.banner_no = s.banner_no
            WHERE MONTH(l.start_date) BETWEEN 4 AND 7
            ORDER BY l.start_date
        """
    },
    {
        "id": 4,
        "label": "(d) Total rent paid by a student",
        "description": "Total rent paid by a given student — enter their banner number",
        "params": [
            {"name": "banner_no", "hint": "e.g. S100", "type": "text"}
        ],
        "sql": """
            SELECT
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                COUNT(i.invoice_no)                    AS total_invoices,
                SUM(i.payment_due)                     AS total_amount_billed,
                SUM(CASE WHEN i.payment_date IS NOT NULL THEN i.payment_due ELSE 0 END) AS total_amount_paid,
                SUM(CASE WHEN i.payment_date IS NULL     THEN i.payment_due ELSE 0 END) AS total_amount_outstanding
            FROM Student s
            LEFT JOIN Invoice i ON s.banner_no = i.banner_no
            WHERE s.banner_no = %s
            GROUP BY s.banner_no, s.first_name, s.last_name
        """
    },
    {
        "id": 5,
        "label": "(e) Unpaid invoices",
        "description": "Students who have not paid their invoices (all outstanding invoices)",
        "params": [],
        "sql": """
            SELECT
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                s.email,
                s.mobile,
                i.invoice_no,
                i.semester,
                i.payment_due,
                i.first_reminder,
                i.second_reminder
            FROM Student s
            JOIN Invoice i ON s.banner_no = i.banner_no
            WHERE i.payment_date IS NULL
              AND i.payment_due > 0
            ORDER BY s.banner_no, i.invoice_no
        """
    },
    {
        "id": 6,
        "label": "(f) Unsatisfactory inspections",
        "description": "Apartment inspections where the property was found to be unsatisfactory (Fail)",
        "params": [],
        "sql": """
            SELECT
                ins.inspection_id,
                a.apartment_id,
                a.address                                     AS apartment_address,
                CONCAT(st.first_name, ' ', st.last_name)      AS inspected_by,
                ins.inspection_date,
                ins.status,
                ins.comments
            FROM Inspection ins
            JOIN Apartment a  ON ins.apartment_id = a.apartment_id
            JOIN Staff st     ON ins.staff_id     = st.staff_id
            WHERE ins.status = 'Fail'
            ORDER BY ins.inspection_date DESC
        """
    },
    {
        "id": 7,
        "label": "(g) Students in a particular hall",
        "description": "Names, banner numbers, room and place numbers for students in a given hall — enter hall name (e.g. Block A)",
        "params": [
            {"name": "hall_name", "hint": "e.g. Block A", "type": "text"}
        ],
        "sql": """
            SELECT
                h.name                                        AS hall_name,
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name)        AS student_name,
                r.room_no,
                r.place_no
            FROM Student s
            JOIN Lease l  ON s.banner_no = l.banner_no
            JOIN Room r   ON l.place_no  = r.place_no
            JOIN Hall h   ON r.hall_id   = h.hall_id
            WHERE h.name = %s
            ORDER BY s.last_name
        """
    },
    {
        "id": 8,
        "label": "(h) Students on waiting list",
        "description": "All students currently on the waiting list (status = 'waiting')",
        "params": [],
        "sql": """
            SELECT
                banner_no,
                CONCAT(first_name, ' ', last_name) AS student_name,
                email,
                mobile,
                category,
                nationality,
                major
            FROM Student
            WHERE status = 'waiting'
            ORDER BY last_name, first_name
        """
    },
    {
        "id": 9,
        "label": "(i) Students per category",
        "description": "Total number of students in each student category (UG / PG / NRI / Foreign)",
        "params": [],
        "sql": """
            SELECT
                category,
                COUNT(*) AS total_students
            FROM Student
            GROUP BY category
            ORDER BY total_students DESC
        """
    },
    {
        "id": 10,
        "label": "(j) Students missing next-of-kin",
        "description": "Names and banner numbers for students who have not supplied next-of-kin details",
        "params": [],
        "sql": """
            SELECT
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                s.email,
                s.mobile
            FROM Student s
            LEFT JOIN Next_Of_Kin nk ON s.banner_no = nk.banner_no
            WHERE nk.banner_no IS NULL
            ORDER BY s.last_name, s.first_name
        """
    },
    {
        "id": 11,
        "label": "(k) Advisor for a student",
        "description": "Name and phone number of the Advisor for a given student — enter banner number",
        "params": [
            {"name": "banner_no", "hint": "e.g. S100", "type": "text"}
        ],
        "sql": """
            SELECT
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                a.full_name                            AS advisor_name,
                a.phone                                AS advisor_phone,
                a.email                                AS advisor_email,
                a.department,
                a.room_no                              AS advisor_room
            FROM Student s
            JOIN Advisor a ON s.advisor_id = a.advisor_id
            WHERE s.banner_no = %s
        """
    },
    {
        "id": 12,
        "label": "(l) Hall room rent statistics",
        "description": "Minimum, maximum and average monthly rent for rooms in residence halls",
        "params": [],
        "sql": """
            SELECT
                MIN(rent)           AS min_rent,
                MAX(rent)           AS max_rent,
                ROUND(AVG(rent), 2) AS avg_rent
            FROM Room
        """
    },
    {
        "id": 13,
        "label": "(m) Total places per hall",
        "description": "Total number of rooms/places in each residence hall",
        "params": [],
        "sql": """
            SELECT
                h.name             AS hall_name,
                COUNT(r.place_no)  AS total_places
            FROM Hall h
            JOIN Room r ON h.hall_id = r.hall_id
            GROUP BY h.hall_id, h.name
            ORDER BY total_places DESC
        """
    },
    {
        "id": 14,
        "label": "(n) Residence staff over 60",
        "description": "Staff ID, name, age and position of all residence staff over 60 years old",
        "params": [],
        "sql": """
            SELECT
                staff_id,
                CONCAT(first_name, ' ', last_name)  AS staff_name,
                TIMESTAMPDIFF(YEAR, dob, CURDATE())  AS age,
                position,
                location
            FROM Staff
            WHERE TIMESTAMPDIFF(YEAR, dob, CURDATE()) > 60
            ORDER BY age DESC
        """
    },
]
