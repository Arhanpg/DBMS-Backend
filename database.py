import mysql.connector
from mysql.connector import Error
from dotenv import load_dotenv
import os
import re

load_dotenv()

# Stateless: one fresh connection per request (works on Vercel + Render)
def get_connection():
    """Opens and returns a single MySQL connection to Aiven."""
    try:
        conn = mysql.connector.connect(
            host=os.getenv("DB_HOST", "mysql-19adb67c-arhanghosarwade05-abb7.a.aivencloud.com"),
            port=int(os.getenv("DB_PORT", 25850)),
            user=os.getenv("DB_USER", "avnadmin"),
            password=os.getenv("DB_PASSWORD", ""),
            database=os.getenv("DB_NAME", "HK"),
            autocommit=True,
            ssl_disabled=False,
            connection_timeout=10
        )
        return conn
    except Error as e:
        raise RuntimeError(f"DB connection failed: {e}")


def execute_query(sql: str, params: tuple = None) -> dict:
    """
    Executes a SQL SELECT query and returns {columns, rows}.
    Falls back to DESCRIBE for empty tables so headers are always returned.
    """
    conn = None
    cursor = None
    try:
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)

        if params:
            cursor.execute(sql, params)
        else:
            cursor.execute(sql)

        rows = cursor.fetchall()

        # Serialize non-JSON-safe types (Decimal, date, datetime, timedelta)
        serialized_rows = []
        for row in rows:
            serialized_row = {}
            for key, value in row.items():
                if value is None:
                    serialized_row[key] = None
                elif isinstance(value, (int, float, bool, str)):
                    serialized_row[key] = value
                else:
                    serialized_row[key] = str(value)
            serialized_rows.append(serialized_row)

        # Derive column names
        if rows:
            columns = list(rows[0].keys())
        else:
            # For empty table SELECT: fall back to cursor.description
            if cursor.description:
                columns = [desc[0] for desc in cursor.description]
            else:
                # Last resort: DESCRIBE the table extracted from SQL
                match = re.search(r'FROM\s+`?(\w+)`?', sql, re.IGNORECASE)
                if match:
                    table_name = match.group(1)
                    cursor.execute(f"DESCRIBE `{table_name}`")
                    desc = cursor.fetchall()
                    columns = [col["Field"] for col in desc]
                else:
                    columns = []

        return {"columns": columns, "rows": serialized_rows}

    except Error as e:
        raise RuntimeError(str(e))
    finally:
        if cursor:
            cursor.close()
        if conn and conn.is_connected():
            conn.close()
