import sys  
from PyQt4.QtGui import *  
from PyQt4.QtCore import *  
from PyQt4.QtWebKit import *  
  
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
    record = False
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
            while indent > 0:
                line = result[index]
                if "{" in line and json == True:
                    indent +=1
                if "}" in line and json == True:
                    indent -=1
                if(indent == 0):
                    prunedresults.append("}")
                else:
                    prunedresults.append(line)
                index += 1
            break
        else:
            index += 1
    for lines in prunedresults:
        print lines
    sys.exit()
if __name__ == "__main__":
   main(sys.argv[1])
    
#url = 'https://patreon.com/vempire'  
#r = Render(url)  
#html = r.frame.toHtml()
#print str(html.toAscii())