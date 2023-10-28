
#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab/IA-Practica-Bicing/R")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res2 <- read.table("exp2H1MEdia.txt", header = TRUE, sep = "\t")

vacia <- subset(res2, sol_ini=="Vacia")
random <- subset(res2, sol_ini=="Random")
greedy <- subset(res2, sol_ini=="Greedy")

#Comparando beneficios
boxplot(vacia$calidad, random$calidad, greedy$calidad, names=c("Vacia", "Aleatoria", "Greedy"), ylab="Calidad")
difVacRand <- sum(vacia$calidad > random$calidad)

difRandGreedy <- sum(greedy$calidad > random$calidad)

#Probabilidad de que pase esto bajo H0
dbinom(x=dif12,size=length(vacia$benef),prob=0.5)

boxplot(vacia$beneficio, random$beneficio, greedy$beneficio, names=c("Vacia", "Aleatoria", "Greedy"), ylab="Beneficio")
boxplot(vacia$beneficio+vacia$calidad, random$beneficio+random$calidad, greedy$beneficio+greedy$calidad, names=c("Vacia", "Aleatoria", "Greedy"), ylab="suma")

#Comparando tiempos
boxplot(vacia$tiempo, random$tiempo, greedy$tiempo, names=c("Vacía", "Aleatoria", "Greedy"), ylab="tiempo (ms)")
t.test(vacia$tiempo, random$tiempo, paired=TRUE)
t.test(random$tiempo, greedy$tiempo, paired=TRUE)

