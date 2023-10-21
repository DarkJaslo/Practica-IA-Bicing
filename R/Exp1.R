#Comparacion de operadores

#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab/IA-Practica-Bicing/R")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res <- read.table("exp1Invent.txt", header = TRUE, sep = "\t")

set1 <- subset(res, set==1)
set2 <- subset(res, set==2)

#Comparando beneficios
boxplot(set1$benef, set2$benef, names=c("set1", "set2"), ylab="beneficio")
dif12 <- sum(set1$benef > set2$benef)

#Probabilidad de que pase esto bajo H0
dbinom(x=dif12,size=length(set1$benef),prob=0.5)

#Comparando tiempos
boxplot(set1$tiempo, set2$tiempo, names=c("set1", "set2"), ylab="ms")
t.test(set1$tiempo, set2$tiempo, paired=TRUE)
