Build the Docker Image:
$ docker build -t thor .

Then run it:
$ docker run thor live SERVER_URL API_KEY TIME_SERVER_URL 

for example:

$ docker run thor live wss://msoll.de/spe_ed 1234567890abcdefghijklmnopqrstuvwxyz https://msoll.de/spe_ed_time

Dont let out arguments, Thor needs them all!