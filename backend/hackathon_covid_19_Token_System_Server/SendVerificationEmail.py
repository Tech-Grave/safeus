# this class is used to generate and send OTP to email and update OTP in the Database.

import math
import random
import smtplib


def generateOTP():
    digits = "0123456789"
    OTP = ""
    for i in range(6):
        OTP += digits[math.floor(random.random() * 10)]
    return OTP


def sendMail(myDB, myCursor, phone, email):
    otp = generateOTP()
    try:
        s = smtplib.SMTP('smtp.gmail.com', 587)
        s.starttls()  # start TLS for security
        s.login("samplehackathonx@gmail.com", "sample4444")
        message = otp + " is your verification code for Safeus.\nPlease enter the " \
                        "verification code and login to your account.\n\nIgnore this message if this action was not " \
                        "performed by you. "
        print('sending message')
        s.sendmail("samplehackathonx@gmail.com", email, message)
        s.quit()
        print('message sent')

        # update OTP field in the receiver's account in database

        # first check record in user, if not found check in owner
        myCursor.execute(
            "UPDATE `tokendatabase`.`user` SET `otp` = '" + otp + "' WHERE(`email` = '" + email + "'" +
            "AND phone = '" + phone + "');"
        )
        myDB.commit()

        # now, check record in owner table
        myCursor.execute(
            "UPDATE `tokendatabase`.`owner` SET `otp` = '" + otp + "' WHERE(`email` = '" + email + "'" +
            "AND phone = '" + phone + "');"
        )
        print('before commit')
        myDB.commit()
        print('commit done')
        return True
    except BaseException as e:
        print('Error: ', e)
        return False


print('Hi')
# sendMail("sendtoashutoshpaul@outlook.com")
