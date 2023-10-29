###################################IMPORANTE####################################
#Escribe en el siguiente comando el path del directorio donde se encuentre la 
#carpeta y después /R
setwd("")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res2 <- read.table("exp2.txt", header = TRUE, sep = "\t")

vacia <- subset(res2, sol_ini=="Vacia")
random <- subset(res2, sol_ini=="Random")
greedy <- subset(res2, sol_ini=="Greedy")

#Comparando calidad
boxplot(vacia$calidad, random$calidad, greedy$calidad, names=c("Vacia", "Aleatoria", "Greedy"), ylab="Calidad")

#Comparando beneficio (en caso de estar usando H2)
boxplot(vacia$beneficio, random$beneficio, greedy$beneficio, names=c("Vacia", "Aleatoria", "Greedy"), ylab="Beneficio")

#Comparando calidad+beneficio
boxplot(vacia$beneficio+vacia$calidad, random$beneficio+random$calidad, greedy$beneficio+greedy$calidad, 
        names=c("Vacia", "Aleatoria", "Greedy"), ylab="Calidad+Beneficio")

#Comparando tiempo
boxplot(vacia$tiempo, random$tiempo, greedy$tiempo, names=c("Vacía", "Aleatoria", "Greedy"), ylab="tiempo (ms)")
t.test(vacia$tiempo, random$tiempo, paired=TRUE)
t.test(random$tiempo, greedy$tiempo, paired=TRUE)

