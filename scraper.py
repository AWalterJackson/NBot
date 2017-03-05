import sys
import getopt
from PyQt4.QtGui import *  
from PyQt4.QtCore import *  
from PyQt4.QtWebKit import *  
from lxml import html 

#Take this class for granted.Just use result of rendering.
class Render(QWebPage):  
  def __init__(self, url):  
    self.app = QApplication(sys.argv)  
    QWebPage.__init__(self)  
    self.loadFinished.connect(self._loadFinished)  
    self.mainFrame().load(QUrl(url))  
    self.app.exec_()  
  
  def _loadFinished(self, result):  
    self.frame = self.mainFrame()  
    self.app.quit()  
    
def main(u):

    url = 'https://patreon.com/'+u+'/'
    r = Render(url)  
    result = r.frame.toHtml()
    #This step is important.Converting QString to Ascii

    result = str(result.toAscii()).split("\n")

    prunedresults = []
    keeprunning = True
    index = 0
    indent = -1
    length = len(result)
    json = False

    while keeprunning:
        line = result[index]
        if "window.patreon.bootstrap.creator" in line:
            indent = 1
            json = True
            prunedresults.append("{")
            index +=1
            continue
        if indent > 0:
            prunedresults.append(line)
        if "{" in line and json == True:
            indent +=1
        if "}" in line and json == True:
            indent -=1
        if indent == 0:
            keeprunning = False
            break
        if index >= length:
            break
        index += 1

    for lines in prunedresults:
        print lines

if __name__ == "__main__":
   print sys.argv
   main(sys.argv[1])