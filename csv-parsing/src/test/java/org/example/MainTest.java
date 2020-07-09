package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTest {
    @Test
    @DisplayName("Main method throws when receiving incorrect arguments")
    public void testArgs() {
        var wrongNumberOfArgsException = assertThrows(IllegalArgumentException.class, () -> Main.main(new String[]{"some stuff"}));
        assertEquals(Main.WRONG_NUM_OF_ARGUMENTS_ERR_MSG, wrongNumberOfArgsException.getMessage());

        var wrongDateFormatException = assertThrows(IllegalArgumentException.class, () ->
                Main.main(new String[] {"some_file.csv", "2020-01-01"}));
        assertEquals(Main.WRONG_DATE_FORMAT_ERR_MSG, wrongDateFormatException.getMessage());
    }

    @Test
    @DisplayName("Main.getNumberOfTrafficBlocks returns right amount")
    public void testGetNumberOfTrafficBlocks() {
        var dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        assertEquals(Main.getNumberOfTrafficBlocks("data.csv", LocalDate.parse("01.01.2013", dateFormatter)), 4);
        assertEquals(Main.getNumberOfTrafficBlocks("data.csv", LocalDate.parse("06.06.2017", dateFormatter)), 173);
    }
}
