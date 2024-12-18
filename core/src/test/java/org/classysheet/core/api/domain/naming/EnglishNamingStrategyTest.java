package org.classysheet.core.api.domain.naming;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnglishNamingStrategyTest {

    record House(String name, String lastName) {}
    record BlueHouse() {}
    record Puppy() {}

    @Test
    void workbookName() {
        NamingStrategy namingStrategy = new EnglishNamingStrategy();
        assertThat(namingStrategy.workbookName(House.class)).isEqualTo("House");
        assertThat(namingStrategy.workbookName(BlueHouse.class)).isEqualTo("Blue house");
        assertThat(namingStrategy.workbookName(Puppy.class)).isEqualTo("Puppy");
    }

    @Test
    void sheetName() {
        NamingStrategy namingStrategy = new EnglishNamingStrategy();
        assertThat(namingStrategy.sheetName(House.class)).isEqualTo("Houses");
        assertThat(namingStrategy.sheetName(BlueHouse.class)).isEqualTo("Blue houses");
        assertThat(namingStrategy.sheetName(Puppy.class)).isEqualTo("Puppies");
    }

    @Test
    void columnName() throws NoSuchFieldException {
        NamingStrategy namingStrategy = new EnglishNamingStrategy();
        assertThat(namingStrategy.columnName(House.class.getDeclaredField("name"))).isEqualTo("Name");
        assertThat(namingStrategy.columnName(House.class.getDeclaredField("lastName"))).isEqualTo("Last name");
    }

}