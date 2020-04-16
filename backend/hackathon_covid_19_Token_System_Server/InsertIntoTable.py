import mysql.connector


def insertOwnerDataIntoDB(myDB, myCursor, shopName, ownerName, shopType, openingTime, closingTime, timeReqPerUser,
                          address, city, pinCode, phone, email, password):
    print('end')
    myDB.autocommit = False
    cursor = myDB.cursor(dictionary=True)
    try:
        cursor.execute(
            "INSERT INTO `tokendatabase`.`owner` (`shop_name`, `owner_name`, `shop_type`, `opening_time`, "
            "`closing_time`, " +
            "`time_req_per_user`, `address`, `city`, `pin_code`, `phone`, `email`, `password`) VALUES ('" + shopName +
            "', '" + ownerName + "', '" + shopType + "', '" + openingTime + "', '" + closingTime + "', '" + timeReqPerUser + "', '" +
            address + "', '" + city + "', '" + pinCode + "', '" + phone + "', '" + email + "', '" + password + "');"
        )
        print('inserted')

        # get shop_id from 'owner' table
        cursor.execute(
            "SELECT shop_id FROM `tokendatabase`.`owner` WHERE email = '" + email + "';"
        )
        print('query executed')
        shop_id = ''
        for x in cursor:
            shop_id = str(x['shop_id'])
            if shop_id is not None:
                break

        print('shop_id: ' + shop_id)
        # now, create separate table 'event'
        cursor.execute(
            "CREATE TABLE `tokendatabase`. `event_" + shop_id + "`(`date` DATE NOT NULL, `user_email` VARCHAR(100) " +
            "NOT NULL, `incoming_time` TIME NOT NULL, `outgoing_time` TIME NULL, `is_event_over` CHAR(1) " +
            "NULL DEFAULT '0', `status` VARCHAR(100) NULL, PRIMARY KEY(`date`, `incoming_time`, `user_email`)); "
        )
        print('event table created')
        myDB.commit()
        print(True)
        return True
    except  mysql.connector.Error as err:
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


def insertUserDataIntoDB(myDB, myCursor, userName, address, city, pinCode, phone, email, password):
    print('end')
    try:
        myCursor.execute(
            "INSERT INTO `tokendatabase`.`user` (`user_name`, `address`, `city`, `pin_code`, `phone`, `email`,"
            " `password`) VALUES ('" + userName + "', '" + address + "', '" + city + "', '" + pinCode + "', '" + phone + "'," +
            "'" + email + "', '" + password + "');"
        )
        myDB.commit()
        return True
    except mysql.connector.Error as error:
        print(error)
        return False


def updatePassword(myDB, myCursor, of, email, password):
    print('end')
    try:
        myCursor.execute(
            "UPDATE `tokendatabase`.`" + of + "` SET `password` = '" + password + "' WHERE(`email` = '" + email + "');"
        )
        myDB.commit()
        return True
    except BaseException as error:
        print('Error in InsertIntoTable.updatePassword: ', error)
        return False


def addBooking(myDB, myCursor, shopId, userEmail, ownerEmail, note, incomingTime, outgoingTime, status):
    # this function inserts user booking details to `task` and `event_<shopId>` table
    try:
        myDB.autocommit = False

        # insert in `task`
        myCursor.execute(
            "INSERT INTO `tokendatabase`.`task`(`user_email`, `owner_email`, `note`, `date`, `incoming_time`, "
            "`outgoing_time`, " 
            "`status`) VALUES('" + userEmail + "', '" + ownerEmail + "', '" + note + "', CURDATE(),'" + incomingTime + "', "
            "'" + outgoingTime + "', 'booked');"
        )

        # insert in `event_<shopId>`
        # currentDate is done using CURDATE() function of database
        myCursor.execute(
            "INSERT INTO `tokendatabase`.`event_" + shopId + "`(`date`, `user_email`, `incoming_time`, "
            "`outgoing_time`, `status`) VALUES(CURDATE(), '" + userEmail + "', '" + incomingTime + "', "
            "'" + outgoingTime + "', '" + status + "');"
        )

        print('Booking details inserted successfully')
        myDB.commit()
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
