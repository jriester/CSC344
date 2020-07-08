import os, subprocess, sys, zipfile, smtplib
import os.path
from lxml import etree
import re
from io import BytesIO
from email.mime.application import MIMEApplication
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText


def wordcounter(filename):
    path = filename
    if os.path.isfile(filename):
        p = subprocess.Popen(["wc", "-l", filename], stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        res = p.communicate()[0].decode('UTF-8')
        trash = res.split()
        print(filename + ": " + str(trash[0]))
        return str(trash[0])


def linecounter(filename):
    ext = filename.split(".")[-1]
    file = open(filename, "r").read().replace(" ", "").replace("\t", "").splitlines()
    lines = 0
    for line in file:
        if len(line) > 0:
            if (ext == "c" or ext == "scala"):
                if (line[:2] != "/*" and line[0] != "*" and line[:2] != "*/" and line[:2] != "//"):
                    lines += 1
            elif (ext == "lisp" and line[0] != ";"):
                lines += 1
            elif (ext == "py" and line[0] != "#"):
                lines += 1
            elif (ext == "pl"):
                if line[:2] != "/*" and line[0] != "*" and line[:2] != "*/" and line[0] != "%":
                    lines += 1

    return lines


def getwords(filename):
    id = set()
    with open(filename, "r") as opener:

        for currLine in opener:
            currLine = re.sub("(\#)+.*(?=\s)", "", currLine)
            currLine = re.sub("\"\s*.*?\s*\"", "", currLine)
            currLine = re.sub("(\%)+.*(?=\s)", "", currLine)
            currLine = re.sub("(\//.*)", "", currLine)
            currLine = re.sub("(\;.*)", "", currLine)
            currLine = re.findall("(\s*[a-zA-Z]*\s*)", currLine)
            for y in currLine:
                y = y.rstrip('\t\r\n')
                y = re.sub("\s+", "", y)
                y = y.rstrip('\n')
                if id.__contains__(y) is False:
                    id.add(y)
    return id


def writeHTML(files, linecount, count, id):
    # Create line count
    html = open("summary_a" + str(count) + ".html", 'w')
    html.write("<html><body bgcolor='F0F8FF'><center><br>")
    html.write("<font size = 6>")
    html.write("Number of lines: " + str(linecount) + "<br>")
    html.write("</body>")

    # Create source file link
    href = "../" + str(count) + "/" + files
    html.write("<font size=6")
    html.write("<br><a href='" + href + "'>" + files + "</a>")
    html.write("<br><br>")

    # Create identifiers list
    html.write("List of identifiers:" + "<br>")
    for identifier in id:
        html.write("<ul>" + identifier)
        html.write("</ul>")

    html.write("</html>")
    # Create index.html page
    html = open('index.html', '+w')
    html.write("<html><body bgcolor='F0F8FF'><center><br>")
    html.write("<font size='12'>")
    html.write("<b>CSC344 : Programming Languages</b><br>")
    html.write("James Riester<br>")
    c = 1
    while c < 6:
        summaries = 'summary_a' + str(c) + ".html"
        html.write("<br><a href=" + summaries + '>Assignment ' + str(c) + '</a>')
        c += 1

    html.write("<br></center></body></html>")
    html.close()



def myzipper(system):
    myzipfile = zipfile.ZipFile("Project5.zip", 'w', zipfile.ZIP_DEFLATED)
    for curr in os.listdir(system):
        if os.path.isdir((system + curr + "/")):
            for filenext in os.listdir(system + "/" + curr):
                myzipfile.write(system + "/" + curr + "/" + filenext, "/Project5/" + curr + "/" + filenext)
        else:
            myzipfile.write(system + "/" + curr, "/nothingishere/" + curr)
    myzipfile.close()


def changeName(zipFile):
    os.rename(zipFile, "Project5.txt")


def emailzipfile(givenemail):
    userName = "344throwaway@gmail.com"
    password = "coolgrass123!"
    myZipFile = "Project5.txt"
    attach = open(myZipFile, "rb")

    msg = MIMEMultipart()
    msg['From'] = userName
    msg['To'] = givenemail

    part = MIMEApplication(attach.read(), Name="Project5.txt")
    msg.attach(part)
    part.add_header('Content-Disposition', "attachment; filename= %s" % myZipFile)

    server = smtplib.SMTP_SSL('smtp.gmail.com', 465)
    server.login(userName, password)
    server.sendmail(userName, givenemail, msg.as_string())
    server.close()


def main():
    my_path = os.path.dirname(__file__)
    #print("my path: " + my_path)
    path = os.path.join(my_path, "C:/Users/James/Desktop/csc344/")
    wordcountlist = []
    wordlist = set()
    i = 1
    j = 0
    files = ["main.c", "main.clj", "main.scala", "main.pl", "main.py"]
    while i < 6:
        n = wordcounter("\project" + str(i) + "/" + files[j])
        wordcountlist.append(n)
        i += 1
        j += 1

    q = 0
    z = 1
    while q < 5:
        #print("FILES AT Q: " + files[q])
        writeHTML(files[q], linecounter(path + "project" + str(q + 1) + "/" + files[q]), z, getwords(path + "project" + str(q + 1) + "/" + files[q]))
        q += 1
        z += 1

    p = 1
    myzipper("../")
    changeName("Project5.zip")
    emailme = input("Email: ")
    emailzipfile(emailme)


if __name__ == "__main__":
    main()

