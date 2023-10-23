#Comparacion de operadores

#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab/IA-Practica-Bicing/R")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res4 <- read.table("exp4.txt", header = TRUE, sep = "\t")

est1 <- subset(res4, estaciones==25)
est2 <- subset(res4, estaciones==50)
est3 <- subset(res4, estaciones==75)
est4 <- subset(res4, estaciones==100)
est5 <- subset(res4, estaciones==125)
est6 <- subset(res4, estaciones==150)
est7 <- subset(res4, estaciones==175)
est8 <- subset(res4, estaciones==200)

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
