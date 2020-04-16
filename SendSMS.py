from twilio.rest import Client

print('Hi')
# Your Account Sid and Auth Token from twilio.com/console
# DANGER! This is insecure. See http://twil.io/secure
account_sid = 'AC36a2752271068689c624b11cc5f6f3b1'
auth_token = '00823ee80708888a3497d7350e8c4258'
client = Client(account_sid, auth_token)
try:
    message = client.messages.create(
        body='This is the ship that made the Kessel Run in fourteen parsecs?',
        from_='+12564488219',
        to='+917870004025'
    )

    print(message.sid)
except:
    print('Number is unverified. Cannot send from Twilio Trial Version')
