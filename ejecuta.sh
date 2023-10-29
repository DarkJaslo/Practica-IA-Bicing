if [ "$#" -eq 9 ]; then
  java -Xmx8192m -cp $(cat classpaths.txt) $1 $1 $2 $3 $4 $5 $6 $7 $8 $9
  exit 0
elif [ "$#" -ne 1 ]; then
  echo "Usage: $0 <MainClassName>"
  echo "Especifica que clase quieres ejecutar como main. Ejemplo: ./ejecuta.sh Tester1"
  exit 1
else
  java -Xmx8192m -cp $(cat classpaths.txt) $1
  exit 0
fi