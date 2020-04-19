# this class is used to send notification alert emails like booking confirmed/cancelled, etc.
# send's email in multi-threading so that Aap doesn't work slow or freezes.

import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText


def sendMailToUser(email, subject, messageToBeSent):
    try:

        senderEmail = "samplehackathonx@gmail.com"
        receiverEmail = email

        message = MIMEMultipart()
        message["From"] = senderEmail
        message["To"] = receiverEmail
        message["Subject"] = subject

        # Add body to email
        body = messageToBeSent
        message.attach(MIMEText(body, "plain"))

        s = smtplib.SMTP('smtp.gmail.com', 587)
        s.starttls()  # start TLS for security
        s.login(senderEmail, "") #enter ur email and password

        text = message.as_string()
        print('sending notification message')
        s.sendmail(senderEmail, email, text)
        s.quit()
        print('message sent')

        return True
    except BaseException as e:
        print('Error: ', e)
        return False
    except smtplib.SMTPException as e:
        print('SMTP error occurred: ' + str(e))
