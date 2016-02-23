#!/bin/sh

# constantes
HOST=localhost 
LOGIN=anonymous
PASSWORD=password
PORT=2121

# le transfert lui même

echo "/////////TESTS ACTIF//////////"

rm testGet 
rm ftp/anonymous/testPut

ftp -d -i -n $HOST $PORT << END_SCRIPT_PORT
quote USER $LOGIN
quote PASS $PASSWORD
pwd
ls
cdup
pwd
cd hi
pwd
cd ..
pwd
get nimportequoi
get testGet
put nanmaisvraiment
put testPut
ls
quit
END_SCRIPT_PORT

cat testGet 
cat ftp/anonymous/testPut

rm testGet 
rm ftp/anonymous/testPut

echo "/////////TESTS PASSIF//////////"

pftp -d -i -n -v $HOST $PORT << END_SCRIPT_PASV
quote USER $LOGIN
quote PASS $PASSWORD
pwd
ls
cdup
pwd
cd hi
pwd
cd ..
pwd
get nimportequoi
get testGet
put nanmaisvraiment
put testPut
ls
quit
END_SCRIPT_PASV

cat testGet 
cat ftp/anonymous/testPut
