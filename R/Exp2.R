
#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res2 <- read.table("exp2Final.txt", header = TRUE, sep = "\t")

vacia <- subset(res2, sol_ini=="Vacia")
random <- subset(res2, sol_ini=="Random")
greedy <- subset(res2, sol_ini=="Greedy")

#Comparando beneficios
boxplot(vacia$calidad, random$calidad, greedy$calidad, names=c("Vacia", "Aleatoria", "Greedy"), ylab="Calidad")
difVacRand <- sum(vacia$calidad > random$calidad)
difRandGreedy <- sum(random$calidad > greedy$calidad)

#Probabilidad de que pase esto bajo H0
dbinom(x=dif12,size=length(vacia$benef),prob=0.5)

boxplot(vacia$beneficio, random$beneficio, greedy$beneficio, names=c("Vacia", "Aleatoria", "Greedy"), ylab="Beneficio")

#Comparando tiempos
boxplot(vacia$tiempo, random$tiempo, greedy$tiempo, names=c("Vacía", "Aleatoria", "Greedy"), ylab="tiempo (ms)")
t.test(vacia$tiempo, greedy$tiempo, paired=TRUE)
t.test(random$tiempo, greedy$tiempo, paired=TRUE)

