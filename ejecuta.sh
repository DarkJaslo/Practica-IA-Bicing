if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <MainClassName>"
  echo "Especifica que clase quieres ejecutar como main. Ejemplo: ./ejecuta Tester1"
  exit 1
fi

java -cp $(cat classpaths.txt) $1