#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab/IA-Practica-Bicing/R")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res5 <- read.table("exp5.txt", header = TRUE, sep = "\t")

HCH1 <- subset(res5, alg=="HC", heur=="H1")
HCH2 <- subset(res5, alg=="HC", heur=="H2")
SAH1 <- subset(res5, alg=="SA", heur=="H1")
SAH2 <- subset(res5, alg=="SA", heur=="H2")

#Comparando beneficios
boxplot(sol1$calidad, sol2$calidad, sol3$calidad, 
        names=c("HC H1", "HC H2", "SA H1", "SA H2"), ylab="beneficio")
dif12 <- sum(sol1$benef > sol2$benef)

#Probabilidad de que pase esto bajo H0
dbinom(x=dif12,size=length(sol1$benef),prob=0.5)

#Comparando tiempos
boxplot(sol1$tiempo, sol2$tiempo, sol3$tiempo, names=c("HC H1", "HC H2", "SA H1", "SA H2"), ylab="ms")
t.test(sol1$tiempo, sol2$tiempo, paired=TRUE)