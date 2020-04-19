# this class checks if tables are created or not, if not then this class creates the tables.
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

import mysql.connector
import time
import _thread as thread
import InsertIntoTable
import SendVerificationEmail
import Search
import SendNotificationEmail


def openConnection():
    myDB = mysql.connector.connect(user="codebuddy", password="", host="127.0.0.1", port=3307)
    # use 3306 for other (your) system(s), no password
    if myDB is not None:
        return myDB
    else:
        return False


def checkForDatabase(myCursor):
    try:
        myCursor.execute("CREATE DATABASE tokendatabase")
    except:
        pass


def checkForTables(myCursor):
    print('checkForTables()')
    try:  # owner table
        print('owner checking')
        myCursor.execute(
            "CREATE TABLE `tokendatabase`.`owner`(" +
            "`shop_id` INT NOT NULL AUTO_INCREMENT, " +
            "`shop_name` VARCHAR(255) NOT NULL, `owner_name` VARCHAR(45) NOT NULL, `shop_type` VARCHAR(45), "
            "`opening_time` TIME NULL, " +
            "`closing_time` TIME NULL, `time_req_per_user` TIME NULL, `address` VARCHAR(200) NULL," +
            "`city` VARCHAR(45) NULL, `pin_code` CHAR(6) NULL, `phone` CHAR(10) NULL, `email` VARCHAR(100) UNIQUE " +
            "NOT NULL, `otp` VARCHAR(45) NULL, `is_verified` CHAR(1) DEFAULT '0', `password` VARCHAR(30) , " +
            "`is_open` CHAR(1) DEFAULT '0', `is_account_active` CHAR(1) DEFAULT '1', PRIMARY KEY(`shop_id`));"
        )
        print("'owner' table created")
        # setting start value for shop_id
        myCursor.execute(
            "ALTER TABLE `tokendatabase`.`owner` AUTO_INCREMENT = 100000;"
        )
        print('owner created')
    except BaseException as err:
        print(err)
        pass
    try:  # user table
        myCursor.execute("CREATE TABLE `tokendatabase`.`user`( `user_name` VARCHAR(45) NOT NULL, "
                         "`address` VARCHAR(200) NULL, `city` VARCHAR(45) NULL, `pin_code` CHAR(6) NULL, "
                         "`phone` CHAR(10) NULL, `email` VARCHAR(100) NOT NULL, `otp` VARCHAR(45) NULL, "
                         "`is_verified` CHAR(1) DEFAULT '0', `password` VARCHAR(30) , `is_account_active` CHAR(1) "
                         "DEFAULT '1', PRIMARY KEY(`email`));")
    except BaseException as err:
        print(err)
        pass
    try:  # task table
        myCursor.execute(
            "CREATE TABLE `tokendatabase`.`task`(" +
            "`user_email` VARCHAR(100) NOT NULL, `owner_email` VARCHAR(100) NOT NULL," +
            "`note` VARCHAR(1000) NULL, `date` DATE, `incoming_time` TIME NOT NULL, "
            "`outgoing_time` TIME NULL, `permission_granted` CHAR(1) NULL DEFAULT '1', "
            "`event_over` CHAR(1) NULL DEFAULT '0', " +
            "`status` VARCHAR(45) NULL, PRIMARY KEY(`incoming_time`, `owner_email`, `user_email`));"
        )
    except:
        pass


def login(loginAs, email, password):
    '''
    :param loginAs: 'user': for user
    :param loginAs: 'owner': for owner
    '''

    # step 1: find account

    myCursor.execute("SELECT * FROM `tokendatabase`.`" + loginAs + "` WHERE email = '" + email + "';")
    userAccount = myCursor.fetchall()
    i = 0
    for x in userAccount:
        i += 1

    if i > 0 :      # account found
        print('account found')
        # step 2: check account is verified or not
        cursor = myDB.cursor(dictionary=True)
        cursor.execute("SELECT is_verified FROM `tokendatabase`.`" + loginAs +"` WHERE email = '" + email + "';")
        validationInfo = cursor.fetchall()

        j = 0
        for x in validationInfo:
            print(x)
            print(x['is_verified'])
            if x['is_verified'] == '1':
                j += 1

        if j > 0:   # account is valid
            print('account is valid')
            # step 3: match password
            cursor.execute("SELECT password from `tokendatabase`.`" + loginAs + "` WHERE email = '" + email + "';")
            isCorrect = False
            passwordInfo = cursor.fetchall()
            for x in passwordInfo:
                if x['password'] == password:
                   isCorrect = True

            if isCorrect == True:       # password matched! account is verified.
                print('password matched')

                # now, update `is_account_active` = 1
                cursor.execute(
                    "UPDATE `tokendatabase`.`" + loginAs + "` SET `is_account_active` = '1' WHERE(`email` = '" + email + "');"
                )
                myDB.commit()
                return True
            else:
                print('password MISMATCH')
                return False
        else:       # account is invalid
            print('account INVALID')
            return False
    else:           # account not found
        print('account not found')
        return False


def verifyOTP(of, email, otp):
    '''
    :param of: 'user' or 'owner'
    '''

    # this function is to verify the OTP(verification code) once the OTP is sent and change 'is_verified' in DB = 1

    cursor = myDB.cursor(dictionary=True)
    cursor.execute("SELECT otp FROM `tokendatabase`.`" + of + "` WHERE email = '" + email + "';")
    otpInfo = cursor.fetchall()
    i = 0

    for x in otpInfo:
        if x['otp'] == otp:
            i += 1
            break
    if i == 1:
        # otp found correct! update DB and set 'is_verified' = 1 (0: not verified)
        try:
            cursor.execute(
                "UPDATE `tokendatabase`.`" + of + "` SET `is_verified` = '1' WHERE(`email` = '" + email + "');")
            myDB.commit()
            return True
        except:
            return False
    else:
        return False


def findAccount(of, phone, email):
    """
    :param of: 'user' or 'owner'
    """

    # this function is used to find accountwhen user/owner forget's their password

    # check email and phone exists or not
    cursor = myDB.cursor(dictionary=True)
    cursor.execute("SELECT * FROM `tokendatabase`.`" + of + "` WHERE email = '" + email + "' AND phone = '" + phone +
                   "';")
    print('entered')
    email = cursor.fetchall()
    i = 0
    for x in email:
        i += 1
    if i > 0:       # account found
        # now, send OTP via email
        print('found')
        return True
    else:           # no such account
        return False


def changePassword(of, otp, email, password):
    """
    :param of: 'user' or 'owner'
    """

    cursor = myDB.cursor(dictionary=True)
    cursor.execute("SELECT otp FROM `tokendatabase`.`" + of + "` WHERE email = '" + email + "'")
    otpInfo = cursor.fetchall()
    isCorrectOTP = False
    print('halfway')
    for x in otpInfo:
        if x['otp'] == otp:
            isCorrectOTP = True
            break

    if isCorrectOTP == True:
        # now, update password in database
        print('otp matched')
        myCursor = myDB.cursor()
        isPasswordUpdated = InsertIntoTable.updatePassword(myDB, myCursor, of, email, password)
        return isPasswordUpdated
    else:
        return False


def deleteAccount(of, email):
    # function to delete (deactivate: set `is_account_active` = '0') user/owner account from database
    try:
        if of == 'user':

            # check if user has any upcoming events
            myCursor.execute(
                "SELECT * FROM `tokendatabase`.`task` WHERE `user_email` = '" + email + "' AND `event_over` = '0';"
            )
            result = myCursor.fetchall()
            remainingCount = 0
            for x in result:
                remainingCount += 1

            if remainingCount > 0:

                # there are upcoming events so user's account cannot be deactivated, else deactivate account
                return 'Account cannot be deactivated as there are upcoming booking(s). Once all upcoming events ' \
                       'are over then please try deactivating account once again.'

            # setting 'is_account_active' = 0
            myCursor.execute(
                "UPDATE `tokendatabase`.`" + of + "` SET `is_account_active` = '0' WHERE(`email` = '" + email + "');"
            )
            myDB.commit()
            return True
        else:   # 'owner'
            # owner cannot delete (deactivate) their account during working hours AND if they have booking(s) pending

            # fetch shop_id from `owner`
            cursor = myDB.cursor(dictionary=True)
            cursor.execute(
                "SELECT `shop_id` FROM `tokendatabase`.`owner` WHERE `email` = '" + email + "';"
            )
            ownerShopId = ''
            result = cursor.fetchall()
            for x in result:
                ownerShopId = x['shop_id']
                break

            print('OwnerShopId:', ownerShopId)

            # if owner has pending slots (`event_<shopId>`.`is_event_over` = 1) then Owner cannot deactivate
            # their account (irrespective of date; THERE SHOULD BE NO PENDING EVENTS)
            cursor.execute(
                "SELECT COUNT(`user_email`) `remaining` FROM `tokendatabase`.`event_" + str(ownerShopId) + "` WHERE "
                "`is_event_over` = '0';"
            )
            remainingUsers = 0     # holds number of remaining user(s)
            result = cursor.fetchall()
            for x in result:
                remainingUsers = x['remaining']

            if remainingUsers == 0:

                # no pending slots/bookings. Hence account can be deleted.
                # setting 'is_account_active' = 0 AND 'is_open' = 0
                myCursor.execute(
                    "UPDATE `tokendatabase`.`" + of + "` SET `is_account_active` = '0', `is_open` = '0' "
                    "WHERE(`email` = '" + email + "');"
                )
                myDB.commit()
                return True
            else:
                return "You cannot delete your account now because there are " + str(remainingUsers) + \
                       " pending bookings to be finished."
    except mysql.connector.Error as err:
        print('MySQL Error:')
        print(err)
        return False
    except BaseException as error:
        print('Error:')
        print(error)
        return False


def updateUserInfo(userName, address, city, pinCode, phone, email):
    # this function is used to update user information (except email)
    myDB.autocommit = False
    try:
        cursor = myDB.cursor()
        cursor.execute(
            "UPDATE `tokendatabase`.`user` SET `user_name` = '" + userName + "' WHERE(`email` = '" + email + "');"
        )
        cursor.execute(
            "UPDATE `tokendatabase`.`user` SET `address` = '" + address + "' WHERE(`email` = '" + email + "');"
        )
        cursor.execute(
            "UPDATE `tokendatabase`.`user` SET `city` = '" + city + "' WHERE(`email` = '" + email + "');"
        )
        cursor.execute(
            "UPDATE `tokendatabase`.`user` SET `pin_code` = '" + pinCode + "' WHERE(`email` = '" + email + "');"
        )
        cursor.execute(
            "UPDATE `tokendatabase`.`user` SET `phone` = '" + phone + "' WHERE(`email` = '" + email + "');"
        )

        print('user information updated')
        myDB.commit()
        return True
    except mysql.connector.Error as err:
        print("MySQL Error:")
        print(err)
        myDB.rollback()
        return False
    except BaseException as error:
        print("Error:" + error)
        myDB.rollback()
        return False
    finally:
        myDB.autocommit = True


def getUserOwnerInfo(of, email):
    """
    :param of: 'owner' or 'user'
    """

    # this function returns user information
    try:
        if of == 'user':        # searching in user table
            myCursor.execute("SELECT `user_name`, `address`, `city`, `pin_code`, `phone`, `email` "
                             "FROM `tokendatabase`.`" + of + "` WHERE `email` = '" + email + "';")

            row_headers = [x[0] for x in myCursor.description]  # this will extract row headers
        else:                   # searching in owner table
            myCursor.execute("SELECT `shop_id`,`shop_name`, `owner_name`, `shop_type`, TIME_FORMAT(`opening_time`, "
                             "'%H:00:00 %p') `opening_time`, "
                             "TIME_FORMAT(`closing_time`, '%H:00:00 %p') `closing_time`, TIME_FORMAT("
                             "`time_req_per_user`, '00:%i:00') `time_req_per_user`, `address`, `city`, `pin_code`, "
                             "`phone`, `email` FROM `tokendatabase`.`" + of + "` WHERE `email` = '" + email + "';")

        row_headers = [x[0] for x in myCursor.description]  # this will extract row headers

        result = myCursor.fetchall()
        for x in result:
            # converting to JSON format
            return dict(zip(row_headers, x))

        return False    # if no such account is found return False
    except mysql.connector.Error as err:
        print("MySQL Error:")
        print(err)
        return False
    except BaseException as error:
        print("Error:" + error)
        return False


def isShopTakingBookings(shopId):
    # this function checks whether shop owner is taking slot bookings or not and returns boolean value respectively
    try:
        cursor = myDB.cursor(dictionary=True)
        cursor.execute(
            "SELECT `is_open` FROM `tokendatabase`.`owner` WHERE `shop_id` = '" + shopId + "';"
        )
        data = ''
        result = cursor.fetchall()
        for x in result:
            data = str(x['is_open'])
            break

        if data == '1':
            print(data)
            return True
        else:
            print(data)
            return False
    except mysql.connector.Error as err:
        print("MySQL Error:")
        print(err)
        return False
    except BaseException as error:
        print("Error:" + error)
        return False


def isBookingDone(shopId, email, note, book):
    # check current queue AND `time_req_per_customer` is exceeding `closing_time` or not.
    # At last, insert booking data to the database
    try:
        # step 1: get opening_time, closing_time, time_req_per_user from `owner` of `shop_id`
        cursor = myDB.cursor(dictionary=True)
        cursor.execute(
            "SELECT TIME_FORMAT(`opening_time`, '%H:00:00') `opening_time`, "
            "TIME_FORMAT(`closing_time`, '%H:00:00') `closing_time`, "
            "TIME_FORMAT(`time_req_per_user`, '00:%i:00') `time_req_per_user` "
            "FROM `tokendatabase`.`owner` WHERE `shop_id` = '" + shopId + "';"
        )
        result = cursor.fetchall()
        openingTime = ''
        closingTime = ''
        timeReqPerUser = ''

        for x in result:
            openingTime = x['opening_time']
            closingTime = x['closing_time']
            timeReqPerUser = x['time_req_per_user']
            break
        print(openingTime)
        print(closingTime)
        print(timeReqPerUser)

        # step 2: fetch current slot `outgoing_time` (Today) from `event_<shop_id>`
        cursor = myDB.cursor(dictionary=True)
        cursor.execute(
            "SELECT MAX(TIME_FORMAT(`outgoing_time`, '%H:%i:00')) `outgoing_time` "
            "FROM `tokendatabase`.`event_" + shopId + "` WHERE `date` = CURDATE();"
        )
        result = cursor.fetchall()
        nextSlotAvailableTime = ''
        for x in result:
            nextSlotAvailableTime = x['outgoing_time']
            break
        if nextSlotAvailableTime is not None:

            # nth person doing booking for the day (today)
            print('Time: ', nextSlotAvailableTime)

            if book is False:

                # only send slot details
                return "Your slot will be booked for " + str(nextSlotAvailableTime) + ". Do you wish to book slot? Press OK to continue."

            # now, compare time: check whether nextSlotAvailableTime + timeReqPerUser < closingTime
            # explanation: current slot which is about to be booked SHOULD NOT EXCEED shop's closingTime
            currentUserIncomingTime = nextSlotAvailableTime
            currentUserOutgoingTime = ''

            # step 1: get current outgoing time
            cursor.execute(
                "SELECT ADDTIME('" + nextSlotAvailableTime + "', '" + timeReqPerUser + "') `current_outgoing_time`;"
            )
            result = cursor.fetchall()
            for x in result:
                currentUserOutgoingTime = x['current_outgoing_time']
                break

            print('currentUserIncomingTime: ', currentUserIncomingTime)
            print('currentUserOutgoingTime: ', currentUserOutgoingTime)

            # step 2: now compare: nextSlotAvailableTime + timeReqPerUser (OR current_outgoing_time) < closingTime
            cursor.execute(
                "SELECT '" + currentUserOutgoingTime + "' <= '" + closingTime + "' `compared_result`;"
            )
            isTimeCompatible = ''      # 0: NO, 1: YES
            result = cursor.fetchall()
            for x in result:
                isTimeCompatible = x['compared_result']
                break

            print('isTimeCompatible:', isTimeCompatible)
            if isTimeCompatible == 1:

                # slot(s) are/is available
                # step 3 (if part): final step, insert data to database (insert to `task` and `event_<shopId>`)

                # first, fetch necessary data (owner: email) from database
                cursor.execute(
                    "SELECT `email` FROM `tokendatabase`.`owner` WHERE `shop_id` = '" + shopId + "';"
                )
                ownerEmail = ''  # holds owner's email
                result = cursor.fetchall()
                for x in result:
                    ownerEmail = x['email']
                    break

                print('Owner\'s Email:', ownerEmail)

                # inserting data to the database

                """
                Before inserting check that the current slot time provided by the program is clashing with
                already booked-slot (of some other shop and the slot timing is clashing each other) or not.
                Hence, in slot-clashing case, slot booking is cancelled.
                """
                isNotClashing = isSlotBookedNotClashing(email, currentUserIncomingTime, currentUserOutgoingTime)
                if isNotClashing is True:
                    isInserted = InsertIntoTable.addBooking(myDB, myCursor, shopId, email, ownerEmail, note,
                                                            currentUserIncomingTime, currentUserOutgoingTime,
                                                            status='slot booked')
                    return isInserted
                else:
                    return 'The slot time provided is clashing with an already booked slot. Hence, current slot ' \
                           'booking is not done. Please try after some time.'
            else:
                return 'No slots available for now.'

        else:

            # first person to make booking for the day
            # since this user is first to book slot for the day (today). Hence, no need to compare time
            nextSlotAvailableTime = openingTime
            print('First person: ', nextSlotAvailableTime)

            if book is False:

                return "Your slot will be booked for " + str(nextSlotAvailableTime) + ". Do you wish to book slot? Press OK to continue."
            # step 3 (else part): final step, insert data to database (insert to `task` and `event_<shopId>`)

            # first, fetch necessary data (owner: email) from database
            cursor.execute(
                "SELECT email FROM `tokendatabase`.`owner` WHERE `shop_id` = '" + shopId + "';"
            )
            ownerEmail = ''  # holds owner's email
            result = cursor.fetchall()
            for x in result:
                ownerEmail = x['email']
                break

            print('Owner\'s Email:', ownerEmail)

            # calculation and fetching outgoingTime
            cursor.execute(
                "SELECT ADDTIME('" + openingTime + "', '" + timeReqPerUser + "') `outgoing_time`;"
            )
            calculatedOutgoingTime = '';
            result = cursor.fetchall()
            for x in result:
                calculatedOutgoingTime = x['outgoing_time']
                break

            # inserting data to the database

            """
            Before inserting check that the current slot time provided by the program is clashing with
            already booked-slot (of some other shop and the slot timing is clashing each other) or not.
            Hence, in slot-clashing case, slot booking is cancelled.
            """
            isNotClashing = isSlotBookedNotClashing(email, nextSlotAvailableTime, calculatedOutgoingTime)
            if isNotClashing is True:
                isInserted = InsertIntoTable.addBooking(myDB, myCursor, shopId, email, ownerEmail, note,
                                                        nextSlotAvailableTime, calculatedOutgoingTime,
                                                        status='slot booked')
                return isInserted
            else:
                return 'The slot time provided is clashing with an already booked slot. Hence, current slot ' \
                       'booking is not done. Please try after some time.'

    except mysql.connector.Error as err:
        print('MySQL Error:')
        print(err)
        return False
    except BaseException as error:
        print('Error:')
        print(error)
        return False


def getTimeInSeconds(time_str):
    # Get Seconds from time.
    h, m, s = time_str.split(':')
    return int(h) * 3600 + int(m) * 60 + int(s)


def isSlotBookedNotClashing(userEmail, currentIncomingTime, currentOutgoingTime):
    """
    This function is used to check that the current slot time provided by the program is clashing with
    already booked-slot (of some other shop and the slot timing is clashing each other) or not. Hence, in
    slot-clashing case, slot booking should be cancelled.

    return True: Slots are NOT clashing. All good.
    return False: Slots are clashing. Slot booking should fail!

    """
    # step 1: check if any previous booking is there for the day (today) or not.
    cursor = myDB.cursor(dictionary=True)
    cursor.execute(
        "SELECT `user_email` FROM `tokendatabase`.`task` WHERE `user_email` = '" + userEmail + "';"
    )
    data = ''
    result = cursor.fetchall()
    for x in result:
        data = x['user_email']
        break

    if data is None:

        # no event(s) found of the user for the day (today). Hence no slot clashing possible.
        return True
    else:

        # event(s) found of the user for the day (today). Hence there might be a chance of slot clashing.
        # step 2: make a list of user event(all events for the day(today)) timing (from incoming_time to outgoing_time).
        # Separated by 00:01:00 (1 minute).
        eventBusyScheduleList = []  # Eg: 06:00:00 - 06:05:00; list = [06:00:00, 06:01:00, 06:02:00,..., 06:04:00]

        cursor.execute(
            "SELECT `incoming_time`, `outgoing_time` FROM `tokendatabase`.`task` WHERE `user_email` = '" + userEmail + "';"
        )
        result = cursor.fetchall()
        for x in result:
            eventIncomingTime = x['incoming_time']
            eventOutgoingTime = x['outgoing_time']
            print(eventIncomingTime, '\t', eventOutgoingTime)

            # now, keep on adding time in 1 minutes delay till eventOutgoingTime (exclusive: don't include).
            # Including eventIncomingTime
            tempTime = getTimeInSeconds(str(eventIncomingTime))
            while True:

                # convert tempTime to HH:MM:SS format and append in the list
                timeToBeAppended = time.strftime('%H:%M:%S', time.gmtime(int(tempTime)))

                eventBusyScheduleList.append(timeToBeAppended)
                tempTime = int(tempTime) + 60
                if tempTime == int(getTimeInSeconds(str(eventOutgoingTime))):

                    # breaking now itself so that eventOutgoingTime is excluded
                    break

        print(eventBusyScheduleList)
        """
        # step 3: we have list of time in which user is busy. Check currentIncomingTime and currentOutgoingTime is 
        present in eventBusyScheduleList or not. If YES then, user cannot be free at that time, hence current booking 
        needs to be cancelled. If NO then, user is free/available and can be present at the destination shop
        """
        if str(currentIncomingTime) not in eventBusyScheduleList and str(currentOutgoingTime) not in eventBusyScheduleList:
            return True
        else:
            return False


def updateOwnerInfo(shopName, ownerName, shopType, openingTime, closingTime, timeReqPerUser, address, city, pinCode, phone, email):
    # this method updates user information in the database(except email)(during working hours account cannot be updated)

    # step 1 : fetch opening_time and closing_time and check whether it is working hour or not
    try:
        cursor = myDB.cursor(dictionary=True)
        cursor.execute(
            "SELECT TIME_FORMAT(`opening_time`, '%H:00:00') `opening_time`, TIME_FORMAT(`closing_time`, '%H:00:00') "
            "`closing_time`, TIME_FORMAT(CURTIME(), '%H:%i:00') `current_time` FROM `tokendatabase`.`owner` "
            "WHERE `email` = '" + email + "';"
        )
        savedOpeningTime = ''
        savedClosingTime = ''
        currentTime = ''
        result = cursor.fetchall()
        for x in result:
            savedOpeningTime = x['opening_time']
            savedClosingTime = x['closing_time']
            currentTime = x['current_time']
            break

        print('savedOpeningTime:', savedOpeningTime)
        print('savedClosingTime:', savedClosingTime)
        print('currentTime:', currentTime)

        # step 2: compare whether currentTime is in between openingTime and closingTime or not
        if not (getTimeInSeconds(savedOpeningTime) <= getTimeInSeconds(currentTime) <= getTimeInSeconds(savedClosingTime)):

            # currentTime is not working hour. Hence, account can be updated.
            try:
                myDB.autocommit = False
                cursor = myDB.cursor()

                # update `owner`
                cursor.execute(
                    "UPDATE `tokendatabase`.`owner` SET `shop_name` = '" + shopName + "' WHERE(`email` = '" + email + "');"
                )
                cursor.execute(
                    "UPDATE `tokendatabase`.`owner` SET `owner_name` = '" + ownerName + "' WHERE(`email` = '" + email + "');"
                )
                cursor.execute(
                    "UPDATE `tokendatabase`.`owner` SET `shop_type` = '" + shopType + "' WHERE(`email` = '" + email + "');"
                )
                cursor.execute(
                    "UPDATE `tokendatabase`.`owner` SET `opening_time` = '" + openingTime + "' WHERE(`email` = '" + email + "');"
                )
                cursor.execute(
                    "UPDATE `tokendatabase`.`owner` SET `closing_time` = '" + closingTime + "' WHERE(`email` = '" + email + "');"
                )
                cursor.execute(
                    "UPDATE `tokendatabase`.`owner` SET `time_req_per_user` = '" + timeReqPerUser + "' WHERE(`email` = '" + email + "');"
                )
                cursor.execute(
                    "UPDATE `tokendatabase`.`owner` SET `address` = '" + address + "' WHERE(`email` = '" + email + "');"
                )
                cursor.execute(
                    "UPDATE `tokendatabase`.`owner` SET `city` = '" + city + "' WHERE(`email` = '" + email + "');"
                )
                cursor.execute(
                    "UPDATE `tokendatabase`.`owner` SET `pin_code` = '" + pinCode + "' WHERE(`email` = '" + email + "');"
                )
                cursor.execute(
                    "UPDATE `tokendatabase`.`owner` SET `phone` = '" + phone + "' WHERE(`email` = '" + email + "');"
                )

                myDB.commit()
                print('Owner account updated.')
                return True
            except mysql.connector.Error as err:
                print('MySQL Error:')
                print(err)
                myDB.rollback()
                return False
            except BaseException as error:
                print('Error:')
                print(error)
                myDB.rollback()
                return False
            finally:
                myDB.autocommit = True
        else:
            return "You cannot update your account during working hours (" + openingTime + " to " + closingTime + ")."
    except mysql.connector.Error as err:
        print('MySQL Error:')
        print(err)
        return False
    except BaseException as error:
        print('Error:')
        print(error)
        return False


def getUserEvents(filter, email):
    """
    :param  filter: 'upcoming' or 'earlier'
    """
    try:
        cast = 0    # used to find events according to filter applied,
        order = 'ASC'
        if filter == 'earlier':
            cast = 1
            order = 'DESC'
        else:
            cast = 0
            order = 'ASC'

        # find events from the datbase
        myCursor.execute(
            "SELECT `own`.`shop_id`, `own`.`shop_name`, `own`.`owner_name` `owner_name`, `own`.`shop_type`, `own`.`address`, "
            "`own`.`city`, `own`.`pin_code`, `own`.`phone`, `own`.`email`, `t`.`note`, "
            "date_format(`t`.`date`, '%W %d, %M %y') `event_day`, "
            "TIME_FORMAT(`t`.`incoming_time`, '%h:%i %p') `incoming_time`, "
            "TIME_FORMAT(`t`.`outgoing_time`, '%h:%i %p') `outgoing_time`, `t`.`status` "
            "FROM `tokendatabase`.`owner` AS `own`, `tokendatabase`.`task` AS `t` "
            "WHERE `t`.`event_over` = '" + str(cast) + "' AND `t`.`user_email` = '" + email + "' "
            "AND `own`.`email` = `t`.`owner_email` "
            "ORDER BY `t`.`date` " + order + ", `t`.`incoming_time` " + order + ";"
        )
        row_headers = [x[0] for x in myCursor.description]  # this will extract row headers

        result = myCursor.fetchall()
        json_data = []
        for x in result:
            # converting to JSON format and appending
            #print(x['owner_name'])
            #print(x)
            json_data.append(dict(zip(row_headers, x)))

        # check json_data and send appropriate message
        if len(json_data) != 0:
            return json_data
        else:
            return 'Empty'
    except mysql.connector.Error as err:
        print('MySQL Error:')
        print(err)
        return False
    except BaseException as error:
        print('Error:')
        print(error)
        return False


def generateToken(userEmail, shopId):
    # this function generates TOKEN number (Token number: n'th slot position of user for the day (today))
    try:
        cursor = myDB.cursor(dictionary=True)

        # The last slot booked will be the Token number as booking was done right now.
        cursor.execute(
            "SELECT COUNT(*) `count_position` FROM `tokendatabase`.`event_" + shopId + "` "
            "WHERE `date` = CURDATE() ORDER BY `incoming_time` ASC;"
        )
        tokenNumber = -1
        result = cursor.fetchall()
        for x in result:
            tokenNumber = x['count_position']
            break

        print('Token Number:', tokenNumber)
        if tokenNumber != -1:
            return True, tokenNumber
        else:
            return 'Token number not generated. But slot is successfully booked.'
    except mysql.connector.Error as err:
        print('MySQL Error:')
        print(err)
        return False, -1
    except BaseException as error:
        print('Error:')
        print(error)
        return False, -1


def start():
    myDB = openConnection()

    if myDB is not None:
        myCursor = myDB.cursor()
        print('start()')
        print(myDB)
        checkForDatabase(myCursor)
        checkForTables(myCursor)
        myDB.commit()
    else:
        print('Database not connected.')


start()
#isBookingSlotTimeAvailable('100003')
# start()

# other methods sent to different classes via this class are defined below
myDB = openConnection()
myCursor = myDB.cursor()


def insertOwnerDataIntoDB(shopName, ownerName, shopType, openingTime, closingTime, timeReqPerUser, address, city,
                          pinCode, phone, email, password):
    isInserted = InsertIntoTable.insertOwnerDataIntoDB(myDB, myCursor, shopName, ownerName, shopType, openingTime,
                        closingTime, timeReqPerUser, address, city, pinCode, phone, email, password)
    return isInserted


def insertUserDataIntoDB(userName, address, city, pinCode, phone, email, password):
    isInserted = InsertIntoTable.insertUserDataIntoDB(myDB, myCursor, userName, address, city, pinCode, phone, email,
                                                        password)
    return isInserted


def sendEmailVerification(phone, email):
    isSent = SendVerificationEmail.sendMail(myDB, myCursor, phone, email)
    return isSent


def searchBox(search):
    # this function returns result based on all potential search elements
    return Search.searchAll(myDB, myCursor, search)


def searchInUserCity(userCity):
    # this function returns all shops present in the user's city
    return Search.searchFilter(myDB, myCursor, userCity, 'city')


def searchNearByShops(userPin):
    # this function search nearby shops on the basis of PIN code
    return Search.searchFilter(myDB, myCursor, userPin, 'pin_code')


def searchByCategory(selectedCategory):
    # this function searches shops on the basis of shop category selected
    return Search.searchFilter(myDB, myCursor, selectedCategory, 'shop_type')


def sendEmailNotificationToUser(userEmail, shopId, tokenNumber):
    # this function is used to email notification details to the user when a new slot is booked

    # fetch the latest booking details from shopId of userEmail
    print('inside sendEmailNotificationToUser')
    cursor = myDB.cursor(dictionary=True)
    cursor.execute(
        "SELECT date_format(`date`, '%W %d, %M %y') `date`, TIME_FORMAT(`incoming_time`, '%h:%i:00 %p') "
        "`incoming_time`, TIME_FORMAT(`outgoing_time`, '%h:%i:00 %p') `outgoing_time`, `status` "
        "FROM `tokendatabase`.`event_" + shopId + "` "
        "WHERE `user_email` = '" + userEmail + "' AND `date` = CURDATE() ORDER BY `incoming_time` DESC;"
    )
    emailDate = ''
    emailIncomingTime = ''
    emailOutgoingTime = ''
    emailStatus = ''
    result = cursor.fetchall()
    for x in result:
        # get the latest data (top-most row) and break
        emailDate = x['date']
        emailIncomingTime = x['incoming_time']
        emailOutgoingTime = x['outgoing_time']
        emailStatus = x['status']
        break

    print('date:', emailDate)
    print('incoming time:', emailIncomingTime)
    print('outgoing time', emailOutgoingTime)
    print('status', emailStatus)

    # message to be sent
    message = f"""Your slot booking is confirmed. Here are the necessary details:
    
    Shop ID: {str(shopId)}
    Incoming Time: {str(emailIncomingTime)}
    Outgoing Time: {str(emailOutgoingTime)}
    Date: {str(emailDate)}
    Status: {str(emailStatus)}
    
    And your Token number is {str(tokenNumber)}
    
    Please be at the destination shop on time. Thank you for choosing Safeus.
    
    Regards,
    Team Safeus.
    Have a good day ahead."""
    subject = 'Your slot has been booked!'

    # sending email notification using multi-threading so that so that the web-app doesn't slow down.
    try:
        thread.start_new_thread(SendNotificationEmail.sendMailToUser, (userEmail, subject, message))
    except BaseException as error:
        print('Multi-threading error:')
        print(error)


def sendEmailNotificationToOwner(userEmail, shopId, tokenNumber):
    # this function is used to email notification details to the owner when a new slot is booked
    pass

# down
def setShopBooking(shopId, variable):
    try:
        myCursor.execute(
            "UPDATE `tokendatabase`.`owner` SET `is_open` = '" + variable + "' WHERE `shop_id` = '" + variable + "';"
        )
        myDB.commit()
        return True
    except mysql.connector.Error as err:
        print('MySQL Error:')
        print(err)
        return False
    except BaseException as error:
        print('Error:')
        print(error)
        return False


def getShopBookings(shopId, filter):
    """
    :param filter: 'upcoming' or 'earlier'
    """
    try:
        myCursor.execute(
            # here
        )
        row_headers = [x[0] for x in myCursor.description]  # this will extract row headers

        result = myCursor.fetchall()
        json_data = []
        for x in result:
            # converting to JSON format and appending
            json_data.append(dict(zip(row_headers, x)))

        # check json_data and send appropriate message
        if len(json_data) != 0:
            return json_data
        else:
            return 'Empty'
    except mysql.connector.Error as err:
        print('MySQL Error:')
        print(err)
        return False
    except BaseException as error:
        print('Error:')
        print(error)
        return False
