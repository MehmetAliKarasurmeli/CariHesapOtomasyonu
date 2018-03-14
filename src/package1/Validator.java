/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package package1;

public class Validator {

    public String nameValidator(String name) {
        for (char letter : name.toCharArray()) {
            if (!Character.isLetter(letter) && !Character.isWhitespace(letter)) {
                name = "";
                break;
            }
        }
        return name;
    }

    public String surnameValidator(String surname) {
        for (char letter : surname.toCharArray() ) {
            if (!Character.isLetter(letter) && !Character.isWhitespace(letter)) {
                surname = "";
                break;
            }
        }
        return surname;
    }

    public String phoneValidator(String phone) {
        for (char number : phone.toCharArray()) {
            if (!Character.isDigit(number)) {
                phone = "";
                break;
            }
        }
        return phone;
    }

    public Double moneyValidator(String money) {
        String a = "";
        for (char letter : money.toCharArray()) {
            if (letter == ',') {
                letter = '.';
            } else if (letter == '.') {

            } else if (!Character.isDigit(letter)) {
                a = "0";
                break;
            }
            a += letter;
        }
        return Double.parseDouble(a);
    }

    public Integer numberValidator(String number) {
        for (char digit : number.toCharArray()) {
            if (!Character.isDigit(digit)) {
                number = "-1";
                break;
            }
        }
        return Integer.parseInt(number);
    }

}
