package test;

import controller.DbConnection;
import controller.ServletStudent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServletStudentTest extends Mockito {
    private ServletStudent servlet;
    private MockHttpServletRequest req;
    private MockHttpServletResponse res;

    /**
     * BeforeEach
     */
    @BeforeEach
    void setUp() {
        servlet = new ServletStudent();
        req = new MockHttpServletRequest();
        res = new MockHttpServletResponse();

        //Flag 1 = registration
        req.addParameter("flag", "1");
    }

    /**
     * Pulizia DB
     */
    @AfterEach
    void cleanDB() throws Exception {
        Connection conn = DbConnection.getInstance().getConn();
        PreparedStatement prep = conn.prepareStatement("DELETE FROM USER WHERE EMAIL=?");
        prep.setString(1, "m.rossi@studenti.unisa.it");
        prep.executeUpdate();
    }

    /**
     * TC1.1_01 Name length
     */
    @Test
    void testRegistrazione_1() {
        req.addParameter("name", "");
        req.addParameter("surname", "Rossi");
        req.addParameter("sex", "M");
        req.addParameter("password", "Chiave123");
        req.addParameter("verifyPassword", "Chiave123");
        req.addParameter("email", "m.rossi@studenti.unisa.it");
        assertThrows(IllegalArgumentException.class, () -> servlet.doPost(req, res));
    }

    /**
     * TC1.1_02 Surname length
     */
    @Test
    void testRegistrazione_2() {
        req.addParameter("name", "Mario");
        req.addParameter("surname", "RossiRossiRossiRossiRossiRossi");
        req.addParameter("sex", "M");
        req.addParameter("password", "Chiave123");
        req.addParameter("verifyPassword", "Chiave123");
        req.addParameter("email", "m.rossi@studenti.unisa.it");
        assertThrows(IllegalArgumentException.class, () -> servlet.doPost(req, res));
    }

    /**
     * TC1.1_03 Sex empty
     */
    @Test
    void testRegistrazione_3() {
        req.addParameter("name", "Mario");
        req.addParameter("surname", "Rossi");
        req.addParameter("sex", "");
        req.addParameter("password", "Chiave123");
        req.addParameter("verifyPassword", "Chiave123");
        req.addParameter("email", "m.rossi@studenti.unisa.it");
        assertThrows(IllegalArgumentException.class, () -> servlet.doPost(req, res));
    }

    /**
     * TC1.1_04 password too short
     */
    @Test
    void testRegistrazione_4() {
        req.addParameter("name", "Mario");
        req.addParameter("surname", "Rossi");
        req.addParameter("sex", "M");
        req.addParameter("password", "Chia");
        req.addParameter("verifyPassword", "Chiave123");
        req.addParameter("email", "m.rossi@studenti.unisa.it");
        assertThrows(IllegalArgumentException.class, () -> servlet.doPost(req, res));
    }

    /**
     * TC1.1_05 verifyPassword ! passowrd
     */
    @Test
    void testRegistrazione_5() {
        req.addParameter("name", "Mario");
        req.addParameter("surname", "Rossi");
        req.addParameter("sex", "M");
        req.addParameter("password", "Chiave123");
        req.addParameter("verifyPassword", "Chiave12345");
        req.addParameter("email", "m.rossi@studenti.unisa.it");
        assertThrows(IllegalArgumentException.class, () -> servlet.doPost(req, res));
    }

    /**
     * TC1.1_06 email too short
     */
    @Test
    void testRegistrazione_6() {
        req.addParameter("name", "Mario");
        req.addParameter("surname", "Rossi");
        req.addParameter("sex", "M");
        req.addParameter("password", "Chiave123");
        req.addParameter("verifyPassword", "Chiave123");
        req.addParameter("email", "m@s.i");
        assertThrows(IllegalArgumentException.class, () -> servlet.doPost(req, res));
    }

    /**
     *  TC1.1_07 email not @studenti.unisa.it
     */
    @Test
    void testRegistrazione_7() {
        req.addParameter("name", "Mario");
        req.addParameter("surname", "Rossi");
        req.addParameter("sex", "M");
        req.addParameter("password", "Chiave123");
        req.addParameter("verifyPassword", "Chiave123");
        req.addParameter("email", "info@smartphonecombo.it");
        assertThrows(IllegalArgumentException.class, () -> servlet.doPost(req, res));
    }

    /**
     *  TC1.1_08 Registration ok
     */
    @Test
    void testRegistrazione_8() throws Exception {
        req.addParameter("name", "Mario");
        req.addParameter("surname", "Rossi");
        req.addParameter("sex", "M");
        req.addParameter("password", "Chiave123");
        req.addParameter("verifyPassword", "Chiave123");
        req.addParameter("email", "m.rossi@studenti.unisa.it");
        servlet.doGet(req, res);
        JSONObject json = (JSONObject) new JSONParser().parse(res.getContentAsString());
        assertEquals((long) json.get("result"), 1L);
    }
}