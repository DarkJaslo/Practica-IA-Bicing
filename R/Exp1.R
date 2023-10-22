#Comparacion de operadores

#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab/IA-Practica-Bicing/R")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res1 <- read.table("exp1.txt", header = TRUE, sep = "\t")

set1 <- subset(res1, operadores=="ChangeSwapAdd")
set2 <- subset(res1, operadores=="ChangeChange2SwapAdd")
set3 <- subset(res1, operadores=="ChangeChange2Change3SwapAdd")

#Comparando beneficios
boxplot(set1$beneficio, set2$beneficio, set3$beneficio, 
        names=c("ChangeSwapAdd", "ChangeChange2SwapAdd", "ChangeChange2Change3SwapAdd"), ylab="beneficio")
dif12 <- sum(set1$benef > set2$benef)

#Probabilidad de que pase esto bajo H0
dbinom(x=dif12,size=length(set1$benef),prob=0.5)

#Comparando tiempos
windows()
boxplot(set1$tiempo, set2$tiempo, set3$tiempo, names=c("set1", "set2"), ylab="ms")
t.test(set1$tiempo, set2$tiempo, paired=TRUE)
