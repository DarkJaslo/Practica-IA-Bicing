#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab/IA-Practica-Bicing/R")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res5 <- read.table("exp5.txt", header = TRUE, sep = "\t")

HCH1 <- subset(res5, alg=="HC" & heur=="H1")
HCH2 <- subset(res5, alg=="HC" & heur=="H2")
SAH1 <- subset(res5, alg=="SA" & heur=="H1")
SAH2 <- subset(res5, alg=="SA" & heur=="H2")

#Comparando beneficios
boxplot(HCH1$calidad, SAH1$calidad, HCH2$calidad, SAH2$calidad, 
        names=c("HC H1", "SA H1", "HC H2", "SA H2"), ylab="Calidad")
boxplot(HCH1$beneficio, SAH1$beneficio, HCH2$beneficio, SAH2$beneficio,
        names=c("HC H1", "SA H1", "HC H2", "SA H2"), ylab="Beneficio")
dif12 <- sum(sol1$benef > sol2$benef)

#Probabilidad de que pase esto bajo H0
dbinom(x=dif12,size=length(sol1$benef),prob=0.5)

#Comparando tiempos
boxplot(HCH1$tiempo, HCH2$tiempo, SAH1$tiempo, SAH2$tiempo, 
        names=c("HC H1", "HC H2", "SA H1", "SA H2"), ylab="Tiempo")
t.test(sol1$tiempo, sol2$tiempo, paired=TRUE)

