from flask import Flask, request, Response
from flask_restful import Resource, Api
from flask_restful.representations import json
import json

import DatabaseHandling
import Validation
from ConvertMessagesToJSON import toJsonFormat as toJson
import SendNotificationEmail

app = Flask(__name__)
api = Api(app)


def atStarting():  # call this function first most
    DatabaseHandling.start()


class SignUpForOwner(Resource):
    def get(self, shopName, ownerName, shopType, openingTime, closingTime, timeReqPerUser, address, city, pinCode,
            phone, email, password, confirmPassword):
        print('received owner info')

        # strip strings and convert to lower-case
        shopName = shopName.strip().lower()
        ownerName = ownerName.strip().lower()
        shopType = shopType.strip().lower()
        openingTime = openingTime.strip()
        closingTime = closingTime.strip()
        timeReqPerUser = timeReqPerUser.strip()
        address = address.strip().lower()
        city = city.strip().lower()
        pinCode = pinCode.strip()
        phone = phone.strip()
        email = email.strip().lower()

        if shopName == '' or ownerName == '' or shopType == '' or openingTime == '' or closingTime == '' or timeReqPerUser == '' or address == '' or city == '' or pinCode == '' or phone == '' or email == '' or password == '':
            return toJson('field(s) found empty')

        # match passwords
        isPasswordMatched = Validation.matchPasswords(password, confirmPassword)
        if isPasswordMatched != 'True':
            return toJson(isPasswordMatched)

        # check pin
        isPinValid = Validation.validatePinCode(pinCode)
        if isPinValid == 'True':

            # check phone
            isPhoneValid = Validation.validatePhone(phone)
            if isPhoneValid == 'True':

                # check email
                isEmailValid = Validation.validateEmail(email)
                if isEmailValid == 'True':

                    # check openingTime
                    isOpeningTimeValid = Validation.validateOpeningClosingTime(openingTime)
                    if isOpeningTimeValid == 'True':

                        # check closingTime
                        isClosingTimeValid = Validation.validateOpeningClosingTime(closingTime)
                        if isClosingTimeValid == 'True':

                            # check timeReqPerUser
                            isTimeReqPerUserValid = Validation.validateTimeRequiredPerUser(timeReqPerUser)
                            if isTimeReqPerUserValid == 'True':

                                # check (by comparing) shop opening and closing time is valid or not
                                isShopTimingValid = Validation.validateCompareOpeningClosingTimeMismatch(openingTime,
                                                                                                         closingTime)
                                if isShopTimingValid == 'True':

                                    # all inputs verified; now, insert data into database
                                    isInserted = DatabaseHandling.insertOwnerDataIntoDB(shopName, ownerName, shopType,
                                                                                        openingTime, closingTime,
                                                                                        timeReqPerUser, address, city,
                                                                                        pinCode,
                                                                                        phone, email, password)
                                    return toJson(isInserted)
                                else:
                                    return toJson(isShopTimingValid)
                            else:
                                return toJson(isTimeReqPerUserValid)
                        else:
                            return toJson(isClosingTimeValid)
                    else:
                        return toJson(isOpeningTimeValid)
                else:
                    return toJson(isEmailValid)
            else:
                return toJson(isPhoneValid)
        else:
            return toJson(isPinValid)


class SignUpForUser(Resource):
    def get(self, userName, address, city, pinCode, phone, email, password, confirmPassword):
        print('received user info')

        # strip strings and convert to lower-case
        userName = userName.strip().lower()
        address = address.strip().lower()
        city = city.strip().lower()
        pinCode = pinCode.strip()
        phone = phone.strip()
        email = email.strip().lower()

        # match passwords
        isPasswordMatched = Validation.matchPasswords(password, confirmPassword)
        if isPasswordMatched != 'True':
            return toJson(isPasswordMatched)

        if userName == '' or address == '' or city == '' or pinCode == '' or phone == '' or email == '' or password == '':
            return toJson('field(s) found empty')

        # check pin
        isPinValid = Validation.validatePinCode(pinCode)
        if isPinValid == 'True':

            # check phone
            isPhoneValid = Validation.validatePhone(phone)
            if isPhoneValid == 'True':

                # check email
                isEmailValid = Validation.validateEmail(email)
                if isEmailValid == 'True':
                    isInserted = DatabaseHandling.insertUserDataIntoDB(userName, address, city, pinCode, phone, email,
                                                                       password)
                    return toJson(isInserted)
                else:
                    return toJson(isEmailValid)
            else:
                return toJson(isPhoneValid)
        else:
            return toJson(isPinValid)


class SendVerificationCode(Resource):
    def get(self, phone, email):

        # strip strings and convert to lower-case
        phone = phone.strip()
        email = email.strip().lower()

        # check phone
        isPhoneValid = Validation.validatePhone(phone)
        if isPhoneValid == 'True':

            # check email
            isEmailValid = Validation.validateEmail(email)
            if isEmailValid == 'True':
                isSent = DatabaseHandling.sendEmailVerification(phone, email)
                return toJson(isSent)
            else:
                return toJson(isEmailValid)
        else:
            return toJson(isPhoneValid)


class Login(Resource):
    def get(self, loginAs, email, password):

        # strip strings and convert to lower-case
        email = email.strip().lower()

        # check email
        isEmailValid = Validation.validateEmail(email)
        if isEmailValid == 'True':
            isCorrect = DatabaseHandling.login(loginAs, email, password)
            if isCorrect == True:
                print('You are logged in ')
                return toJson(True)
            else:
                return toJson('Check validity, password, email and try again')
        else:
            return toJson(isEmailValid)


class VerifyOTP(Resource):
    # this class is used to verify otp once VerifyOTP button is pressed
    def get(self, of, email, otp):
        # strip strings and convert to lower-case
        of = of.strip().lower()
        email = email.strip().lower()
        otp = otp.strip()

        # check email
        isEmailValid = Validation.validateEmail(email)
        if isEmailValid == 'True':
            isVerified = DatabaseHandling.verifyOTP(of, email, otp)
            return toJson(isVerified)
        else:
            return toJson(isEmailValid)


class FindAccount(Resource):
    # this class is used to find account of user/owner who have forgotten their password
    # later reset password (by sending email) has been done here (in this class) itself

    def get(self, of, phone, email):
        # strip strings and convert to lower-case
        of = of.strip().lower()
        phone = phone.strip()
        email = email.strip().lower()

        # check phone
        isPhoneValid = Validation.validatePhone(phone)
        if isPhoneValid == 'True':

            # check email
            isEmailValid = Validation.validateEmail(email)
            if isEmailValid == 'True':
                isSent = DatabaseHandling.findAccount(of, phone, email)

                # OTP sent. Display message: OTP has been sent. If not received then press findAccount button again
                DatabaseHandling.sendEmailVerification(phone, email)
                return toJson(isSent)
            else:
                return toJson(isEmailValid)
        else:
            return toJson(isPhoneValid)


class ResetPassword(Resource):
    # This class is used to simply reset password by matching OTP (already sent) and updating password.
    # Should be STRICTLY called after FindAccount class explicitly (from frontend once account is found)

    def post(self, of, email, otp, password, confirmPassword):
        # strip strings and convert to lower-case
        of = of.strip().lower()
        email = email.strip().lower()
        otp = otp.strip()

        # match passwords
        isPasswordMatched = Validation.matchPasswords(password, confirmPassword)
        if isPasswordMatched != 'True':
            return toJson(isPasswordMatched)

        # check email
        isEmailValid = Validation.validateEmail(email)
        if isEmailValid == 'True':
            isPasswordChanged = DatabaseHandling.changePassword(of, otp, email, password)
            return toJson(isPasswordChanged)
        else:
            return toJson(isEmailValid)


class DeleteAccount(Resource):
    # this class is used to delete (deactivate: set `is_account_active` = '0')user/owner account

    def post(self, of, email):
        # strip strings and convert to lower-case
        of = of.strip().lower()
        email = email.strip().lower()

        # check email
        isEmailValid = Validation.validateEmail(email)
        if isEmailValid == 'True':
            isDeleted = DatabaseHandling.deleteAccount(of, email)
            return toJson(isDeleted)
        else:
            return toJson(isEmailValid)


class SearchBox(Resource):

    # this class accepts search requests made in the search box
    def get(self, search):
        # strip search and convert to lower-case
        search = search.strip().lower()

        searchResult = DatabaseHandling.searchBox(search)
        if searchResult is not 'Empty' and searchResult is not False:
            return searchResult
        else:
            return toJson(searchResult)


class SearchInUserCity(Resource):

    # this class searches shops in user's city
    def get(self, userCity):
        # strip search and convert to lower-case
        userCity = userCity.strip().lower()

        searchResult = DatabaseHandling.searchInUserCity(userCity)
        if searchResult is not 'Empty' and searchResult is not False:
            return searchResult
        else:
            return toJson(searchResult)


class SearchNearByShops(Resource):

    # this function search nearby shops on the basis of PIN code
    def get(self, userPin):
        # strip search and convert to lower-case
        userPin = userPin.strip().lower()

        searchResult = DatabaseHandling.searchNearByShops(userPin)
        if searchResult is not 'Empty' and searchResult is not False:
            return searchResult
        else:
            return toJson(searchResult)


class SearchByCategory(Resource):

    # this function searches shops on the basis of shop category selected
    def get(self, selectedCategory):
        # strip search and convert to lower-case
        selectedCategory = selectedCategory.strip().lower()

        searchResult = DatabaseHandling.searchByCategory(selectedCategory)
        if searchResult is not 'Empty' and searchResult is not False:
            return searchResult
        else:
            return toJson(searchResult)


class AboutApp(Resource):

    # this function returns the idea/description of the web-application
    def get(self):
        idea_description = "Since we are facing critical situation due to the COVID 19 pandemic. To win against this " \
                           "pandemic and to come out of this situation most crucial thing we have to follow is " \
                           "social-distancing. But, the problem is people are panicking and are rushing to get their " \
                           "basic daily-life commodities like milk, water, grocery, medicines etc. to the nearby " \
                           "shop's and coming in contact with various people at the cash counters. Because of this " \
                           "behaviour, gathering at the same place (mostly at the same time) they are risking their " \
                           "as well as others life by not following SOCIAL DISTANCING. **" \
                           "So we came up with an idea to deal with the present situation and making things " \
                           "happen in a smooth and DISCIPLINED manner for the people. We are developing an automated " \
                           "system by which every individual can get their essential commodities without being in " \
                           "crowd and waiting in queues outside of shops. In our system Safeus we are providing " \
                           "features so that every indivual can find shops nearby their location as per their " \
                           "requirement. **" \
                           "In our webapp, we are creating two interfaces, one for the buyers' and other for the " \
                           "sellers. We would be taking shop opening and closing time and time required per customer " \
                           "for one transaction. With the help of this data, we would provide slots to users so " \
                           "that as per the slot given time the shop would be empty of other customers. Slots " \
                           "would be given as per availability, availability of commodities needed (by the user) " \
                           "and seller's permission. **" \
                           "It would also help social workers who are arranging food and other commodities for " \
                           "charity and distribution among the less-priviledged as they could pre-book a slot and " \
                           "arrange their huge requirement of goods at an empty shop (with no crowd) in an easy " \
                           "and hassle-free manner. **" \
                           "By this, their won't be any crowd and smooth marketing will take place in a well " \
                           "disciplined manner. Complete eradication of waiting time and roaming of people from " \
                           "one shop to another in search of their commodity. At last we strongly believe that " \
                           "this way we can stop and slow down the spread of the virus. ***" \
                           "#letsmakeindiasafe #staysafe"

        # '*' in idea_description if for client to decode as '\n' and give new line.
        return toJson(idea_description)


class UpdateUserInformation(Resource):

    # this function updates user information in the database (except email)
    def post(self, userName, address, city, pinCode, phone, email):
        # strip strings and convert to lower-case
        userName = userName.strip().lower()
        address = address.strip().lower()
        city = city.strip().lower()
        pinCode = pinCode.strip().lower()
        phone = phone.strip().lower()

        # check pinCode
        isPinValid = Validation.validatePinCode(pinCode)
        if isPinValid == 'True':

            # check phone
            isPhoneValid = Validation.validatePhone(phone)
            if isPhoneValid == 'True':

                # now, update data to the database
                isUpdated = DatabaseHandling.updateUserInfo(userName, address, city, pinCode, phone, email)
                return toJson(isUpdated)
            else:
                return toJson(isPhoneValid)
        else:
            return toJson(isPinValid)


class GetUserOwnerInformation(Resource):

    # this function returns user/owner information to the client
    def get(self, of, email):
        # strip strings and convert to lower-case
        email = email.strip().lower()
        of = of.strip().lower()

        isDataFetched = DatabaseHandling.getUserOwnerInfo(of, email)
        if isDataFetched is not False:
            return isDataFetched
        else:
            return toJson(isDataFetched)


class BookSlot(Resource):
    # function to book a slot
    def get(self, email, shopId, note, book):
        """
        :param book: '1': book slot, '0': ask slot details
        """
        # strip strings and convert to lower-case
        email = email.strip().lower()
        shopId = shopId.strip()
        note = note.strip()
        book = book.strip().lower()

        if book == '0':
            book = False
        else:
            book = True

        # step 1: check isShopTakingBookings
        isShopTakingBookings = DatabaseHandling.isShopTakingBookings(shopId)
        print(isShopTakingBookings)
        if isShopTakingBookings is True:

            # step 2: check current queue AND `time_req_per_customer` is exceeding `closing_time` or not
            # and insert data in the database

            """
            Before inserting check that the current slot time provided by the program is clashing with
            already booked-slot (of some other shop and the slot timing is clashing each other) or not.
            Hence, in slot-clashing case, slot booking is cancelled.
            """
            isBookingDone = DatabaseHandling.isBookingDone(shopId, email, note, book)
            if isBookingDone is True:

                # booking is done successfully
                # now, generate TOKEN number for user (Token number: n'th slot position of user for the day (today))
                isTokenGenerated, tokenNumber = DatabaseHandling.generateToken(email, shopId)
                if isTokenGenerated is True:

                    # token generated. token generated will be sent to the user via email

                    # sending email to user and owner
                    DatabaseHandling.sendEmailNotificationToUser(email, shopId, tokenNumber)
                    DatabaseHandling.sendEmailNotificationToOwner(userEmail=email, shopId=shopId, tokenNumber=tokenNumber)

                    # return isBookingDone (True) rather than token number or email sent status
                    return toJson(isBookingDone)
                else:
                    return toJson("True")       # this statement will be never executed
            else:
                return toJson(isBookingDone)
        else:
            return toJson('Shop is currently not taking any booking requests.')


class UpdateOwnerInformation(Resource):
    def post(self, shopName, ownerName, shopType, openingTime, closingTime, timeReqPerUser, address, city, pinCode,
             phone, email):
        # this function updates owner's information in the database (except email, shop_id).
        # owner cannot update account during working hours

        # strip strings and convert to lower-case
        email = email.strip()
        shopName = shopName.strip().lower()
        ownerName = ownerName.strip().lower()
        shopType = shopType.strip().lower()
        openingTime = openingTime.strip().lower()
        closingTime = closingTime.strip().lower()
        timeReqPerUser = timeReqPerUser.strip().lower()
        address = address.strip().lower()
        city = city.strip().lower()
        pinCode = pinCode.strip().lower()
        phone = phone.strip().lower()

        # check openingTime
        isOpeningTimeValid = Validation.validateOpeningClosingTime(openingTime)
        if isOpeningTimeValid == 'True':

            # check closingTime
            isClosingTimeValid = Validation.validateOpeningClosingTime(closingTime)
            if isClosingTimeValid == 'True':

                # check timeReqPerUser
                isTimeReqPerUserValid = Validation.validateTimeRequiredPerUser(timeReqPerUser)
                if isTimeReqPerUserValid == 'True':

                    # check pinCode
                    isPinCodeValid = Validation.validatePinCode(pinCode)
                    if isPinCodeValid == 'True':

                        # check phone
                        isPhoneValid = Validation.validatePhone(phone)
                        if isPhoneValid == 'True':

                            # check (by comparing) shop opening and closing time is valid or not
                            isShopTimingValid = Validation.validateCompareOpeningClosingTimeMismatch(openingTime,
                                                                                                     closingTime)
                            if isShopTimingValid == 'True':

                                # update data in the database (account cannot be updated during working hours)
                                isUpdated = DatabaseHandling.updateOwnerInfo(shopName, ownerName, shopType, openingTime,
                                                    closingTime, timeReqPerUser, address, city, pinCode, phone, email)
                                return toJson(isUpdated)
                            else:
                                return toJson(isShopTimingValid)
                        else:
                            return toJson(isPhoneValid)
                    else:
                        return toJson(isPinCodeValid)
                else:
                    return toJson(isTimeReqPerUserValid)
            else:
                return toJson(isClosingTimeValid)
        else:
            return toJson(isOpeningTimeValid)


class GetUserEvents(Resource):
    def get(self, filter, email):
        """
        :param filter: 'upcoming' or 'earlier'
        """

        # strip strings and convert to lower-case
        filter = filter.strip().lower()
        email = email.strip().lower()

        # get events
        getEvents = DatabaseHandling.getUserEvents(filter, email)
        if getEvents is not 'Empty' and getEvents is not False:
            return getEvents
        else:
            return toJson(getEvents)


# down

class ShopOpenCloseBooking(Resource):
    def get(self, shopId, variable):
        # set `is_open` as 0/1 in `owner`
        isSet = DatabaseHandling.setShopBooking(shopId, variable)
        return toJson(isSet)


class SeeBookings(Resource):
    def get(self, shopId, filter):
        """
        :param filter: 'upcoming' or 'earlier'
        """
        getBookings = DatabaseHandling.getShopBookings(shopId, filter)
        return toJson(getBookings)



# links
api.add_resource(SignUpForOwner, '/signup/owner/<string:shopName>/<string:ownerName>/<string:shopType>/'
                                 '<string:openingTime>/<string:closingTime>/<string:timeReqPerUser>/<string:address>/'
                                 '<string:city>/<string:pinCode>/<string:phone>/<string:email>/<string:password>/'
                                 '<string:confirmPassword>')
api.add_resource(SignUpForUser, '/signup/user/<string:userName>/<string:address>/<string:city>/<string:pinCode'
                                '>/<string:phone>/<string:email>/<string:password>/<string:confirmPassword>')
api.add_resource(SendVerificationCode, '/sendOTP/<string:phone>/<string:email>')
api.add_resource(Login, '/login/<string:loginAs>/<string:email>/<string:password>')
api.add_resource(VerifyOTP, '/verifyotp/<string:of>/<string:email>/<string:otp>')
api.add_resource(FindAccount, '/findaccount/<string:of>/<string:phone>/<string:email>')
api.add_resource(ResetPassword, '/resetpassword/<string:of>/<string:email>/<string:otp>/<string:password>/'
                                '<confirmPassword>')
api.add_resource(DeleteAccount, '/deleteaccount/<string:of>/<string:email>')        # deactivate account
api.add_resource(SearchBox, '/search/box/<string:search>')
api.add_resource(SearchInUserCity, '/search/city/<string:userCity>')
api.add_resource(SearchNearByShops, '/search/nearby/<string:userPin>')
api.add_resource(SearchByCategory, '/search/category/<string:selectedCategory>')
api.add_resource(AboutApp, '/about')
api.add_resource(UpdateUserInformation, '/update/user/<string:userName>/<string:address>/<string:city>/<string'
                                        ':pinCode>/<string:phone>/<string:email>')
api.add_resource(GetUserOwnerInformation, '/information/<string:of>/<string:email>')
api.add_resource(BookSlot, '/bookslot/<string:email>/<string:shopId>/<string:note>/<string:book>')
api.add_resource(UpdateOwnerInformation, '/update/owner/<string:shopName>/<string:ownerName>/<string:shopType>/'
                                         '<string:openingTime>/<string:closingTime>/<string:timeReqPerUser>/'
                                         '<string:address>/<string:city>/<string:pinCode>/<string:phone>/'
                                         '<string:email>')
api.add_resource(GetUserEvents, '/events/<string:filter>/<string:email>')

# down
api.add_resource(ShopOpenCloseBooking, '/shop/<string:variable>')


if __name__ == '__main__':
    atStarting()
    app.run(debug=True)
