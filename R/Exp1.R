#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab/IA-Practica-Bicing/R")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res1 <- read.table("exp1H1.txt", header = TRUE, sep = "\t")

set1 <- subset(res1, operadores=="ChangeSwapAdd")
set2 <- subset(res1, operadores=="ChangeChange2SwapAdd")
set3 <- subset(res1, operadores=="ChangeChange2Change3SwapAdd")

#Comparando calidad
boxplot(set1$calidad, set2$calidad, set3$calidad, 
        names=c("Conjunto 1", "Conjunto 2", "Conjunto 3"), ylab="Calidad")

#Comparando beneficio
boxplot(set1$beneficio, set2$beneficio, set3$beneficio, 
        names=c("Conjunto 1", "Conjunto 2", "Conjunto 3"), ylab="Beneficio")

#Comparando tiempos
boxplot(set1$tiempo, set2$tiempo, set3$tiempo, names=c("Conjunto 1", "Conjunto 2", "Conjunto 3"), ylab="tiempo (ms)")
t.test(set1$tiempo, set2$tiempo, paired=TRUE)
t.test(set2$tiempo, set3$tiempo, paired=TRUE)
