#Comparacion de operadores

#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res <- read.table("exp2Invent.txt", header = TRUE, sep = "\t")

sol1 <- subset(res, set==1)
sol2 <- subset(res, set==2)

#Comparando beneficios
boxplot(sol1$benef, sol2$benef, names=c("sol1", "sol2"), ylab="beneficio")
dif12 <- sum(sol1$benef > sol2$benef)

#Probabilidad de que pase esto bajo H0
dbinom(x=dif12,size=length(sol1$benef),prob=0.5)

#Comparando tiempos
boxplot(sol1$tiempo, sol2$tiempo, names=c("sol1", "sol2"), ylab="ms")
t.test(sol1$tiempo, sol2$tiempo, paired=TRUE)