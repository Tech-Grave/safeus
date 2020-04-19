"""
This is the search class. When user presses the search button in the search bar(box). Then this class is used to
return the relevant search details.
It is capable of searching:
1. Search shop by shop_id
2. Search shop by shop_name
3. Search shop by owner_name
4. Search shop by phone
5. Search shop by email

6. Search shop(s) by pin_code
7. Search shop(s) by city
8. Search shop by by address

9. Search shop(s) by category

from searchAll(myDB, myCursor, search)
"""

import mysql.connector

select = "`shop_id`, `shop_name`, `owner_name`, `shop_type`, TIME_FORMAT(`opening_time`, '%h:00 %p') " \
         "`opening_time`, TIME_FORMAT(`closing_time`, '%h:00 %p') `closing_time`, " \
         "TIME_FORMAT(`time_req_per_user`, '00:%i mins') `time_req_per_user`, " \
         "`address`, `city`, `pin_code`, `phone`, `email`, `is_open` "


def searchAll(myDB, myCursor, search):

    # this function returns result based on all potential search elements
    try:
        myCursor = myDB.cursor()
        myCursor.execute("SELECT " + select + " FROM `tokendatabase`.`owner`  WHERE `shop_id` = '" + search + "' OR `shop_name` = '" + search + "' OR `owner_name` = '" + search + "' OR `phone` = '" + search + "' OR `pin_code` = '" + search + "' OR `city` = '" + search + "' OR `address` = '" + search + "' OR `shop_type` = '" + search + "' OR `address` LIKE '%" + search + "%'  OR `shop_name` LIKE '%" + search + "%' OR `owner_name` LIKE '%" + search + "%' OR `email` = '" + search + "' ORDER BY `is_open` DESC;")

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
        print("MySQL Error:")
        return False
    except BaseException as error:
        print(error)
    return False


# this function returns shops according to filter detail(s)
def searchFilter(myDB, myCursor, searchElement, filter):
    '''
    :param filter: 'city' for city, 'pin' for PIN Code, 'category' for category
    '''

    try:
        myCursor = myDB.cursor()
        myCursor.execute("SELECT " + select + " FROM `tokendatabase`.`owner`  WHERE `" + filter + "` = '" + searchElement + "' OR `" + filter + "` LIKE '%" + searchElement + "%';")

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
        print("MySQL Error:")
        return False
    except BaseException as error:
        print(error)
        return False
