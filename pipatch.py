import sys
from subprocess import Popen, PIPE

#This file is renamed to scraper.py and calls the normal scraper.py using
#xcfb on a raspberry pi to allow NBot to run when the pi is in CLI mode

def main(a):
    process = Popen(["xvfb-run", "python", "scraper1.py", a], stdout=PIPE)
    (output, err) = process.communicate()
    exit_code = process.wait()
    return output

if __name__ == "__main__":
   main(sys.argv[1])