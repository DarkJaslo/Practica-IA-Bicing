#Comparacion de operadores

#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res <- read.table("exp1Invent.txt", header = TRUE, sep = "\t")

set1 <- subset(res, set==1)
set2 <- subset(res, set==2)

#Comparando beneficios
dif12 <- sum(set1$benef > set2$benef)

#Probabilidad de que pase esto bajo H0
dbinom(x=dif12,size=length(set1$benef),prob=0.5)


#Comparando tiempos
t.test(set1$tiempo, set2$tiempo,paired=TRUE)