sudo su
make
rmmod mastermind
insmod mastermind.ko mmind_number="4283" mmind_max_guesses=15
mknod /dev/mastermind c 250 0
chmod 666 /dev/mastermind
echo "2483" >> /dev/mastermind
cat /dev/mastermind

gcc test_guesscount.c -o test_guesscount
./test_guesscount

gcc test_endgame.c -o test_endgame
./test_endgame

gcc test_newgame.c -o test_newgame
./test_newgame 1542

------------------------------------------------------------------------------------
mmind_max_guesses parameter is optional for insmod. If not entered, it is 10 as 
default.

