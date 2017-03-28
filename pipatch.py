from subprocess import Popen, PIPE

def main(a):
    process = Popen(["xvfb-run", "python", "scraper1.py", a], stdout=PIPE)
    (output, err) = process.communicate()
    exit_code = process.wait()
    return output

if __name__ == "__main__":
   main(sys.argv[1])