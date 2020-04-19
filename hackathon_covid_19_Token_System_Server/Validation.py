# this class is used to evalute pin code, phone, etc. which are provided by the user before sending the data into
# database

import re


def validatePinCode(pin):
    # checks entered pin code is 6 digit numerical value or not
    # step 1: check length is 6 or not

    if pin.__len__() == 6:
        print('yay')
        # step 2: convert to int() type; if converted then pin is numeric else non-numeric

        try:
            pin = int(pin)  # pin is numeric
            return 'True'
        except:
            return 'invalid PIN code'
    else:
        return 'invalid PIN code'


def validatePhone(phone):
    # checks entered phone number is 10 digit numerical value or not

    if phone.__len__() == 10:
        print('yay')
        # step 2: convert to int() type; if converted then phone is numeric else non-numeric

        try:
            pin = int(phone)  # phone is numeric
            print('True')
            return 'True'
        except:
            print('invalid phone number')
            return 'invalid phone number'
    else:
        print('invalid phone number')
        return 'invalid phone number'


def validateEmail(email):
    # checks entered email that it has only one '@' symbol

    count = email.count("@")
    if count == 1:
        print('valid email')
        return 'True'
    else:
        print('invalid email')
        return 'invalid email'


def validateTime(time):
    # checks entered email is of HH:MM:SS format

    # step 1: split in three parts using ':'
    elements = re.split(':', time)

    # step 2: check length
    if len(time) == 8:
        i = 0
        for x in elements:
            try:
                num = int(x)  # convert to number
                if i == 0:  # check hour (HH)
                    if 0 <= num <= 23:
                        i = 1
                    else:
                        return 'invalid time'
                elif i == 1:  # check min (MM)
                    if 0 <= num <= 59:
                        i = 2
                    else:
                        return 'invalid time'
                else:  # check sec (SS)
                    if 0 <= num <= 59:
                        i = 0
                        return 'True'
                    i = 0
            except:
                return 'invalid time'
    else:
        return 'invalid time'


def validateOpeningClosingTime(time):

    # check time is entered in correct format or not
    isFormatValid = validateTime(time)
    if isFormatValid == 'True':

        # now, check it is in format: HH:00:00
        elements = re.split(':', time)
        if elements[1] == '00' and elements[2] == '00':
            return 'True'
        else:
            return 'Enter only hour (HH:00:00) in opening/closing time'
    else:
        return isFormatValid


def validateTimeRequiredPerUser(time):

    # check time is entered in correct format or not
    isFormatValid = validateTime(time)
    if isFormatValid == 'True':

        # now, check it is in format: 00:MM:00
        elements = re.split(':', time)
        if elements[0] == '00' and elements[2] == '00':

            # now, check minute is a multiple of 5 or not
            if int(elements[1]) % 5 == 0:
                if int(elements[1]) != 0:
                    return 'True'
                else:
                    return 'Time required cannot be kept 0 minutes.'
            else:
                return 'Enter minutes (time required per customer) in multiples of 5'
        else:
            return 'Enter only minutes (00:MM:00) in time required per customer'
    else:
        return isFormatValid


def validateCompareOpeningClosingTimeMismatch(openingTime, closingTime):

    # this method compares openingTime and closingTime should not be same AND openingTime < closingTime
    isOpeningTimeValid = validateOpeningClosingTime(openingTime)
    if isOpeningTimeValid == 'True':
        isClosingTimeValid = validateOpeningClosingTime(closingTime)
        if isClosingTimeValid == 'True':

            # both time formats are valid, now split them to get hour
            elementsOpening = re.split(':', openingTime)
            elementsClosing = re.split(':', closingTime)

            # at last, compare and send back result
            if elementsOpening[0] != elementsClosing[0]:
                if elementsOpening[0] < elementsClosing[0]:
                    return 'True'
                else:
                    return 'Shop/Office is opening after it is closed! Please rectify opening and closing time.'
            else:
                return 'Opening time and closing time found to be same. Please check.'
        else:
            return isClosingTimeValid
    else:
        return isOpeningTimeValid


def matchPasswords(password, confirmPassword):
    if password == confirmPassword:
        return 'True'
    else:
        return 'Passwords did not match'

# validateTimeRequiredPerUser('00:55:00')
