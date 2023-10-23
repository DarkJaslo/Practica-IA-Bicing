#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab/IA-Practica-Bicing/R")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res7 <- read.table("exp7.txt", header = TRUE, sep = "\t")

equi7 <- subset(res7, demanda=="equilibrada")
punta7 <- subset(res7, demanda=="horaPunta")

equiCalidadMean <- aggregate(x=equi7$calidad, by=list(equi7$furgonetas), FUN=mean)
equiBenefMean <- aggregate(x=equi7$beneficio, by=list(equi7$furgonetas), FUN=mean)
puntaCalidadMean <- aggregate(x=punta7$calidad, by=list(punta7$furgonetas), FUN=mean)
puntaBenefMean <- aggregate(x=punta7$beneficio, by=list(punta7$furgonetas), FUN=mean)

plot(equiCalidadMean$x ~ equiCalidadMean$Group.1, type="l", ylab="Beneficio h1", xlab="#Furgonetas")
plot(equiBenefMean$x ~ equiBenefMean$Group.1, type="l", ylab="Beneficio h2", xlab="#Furgonetas")

plot(puntaCalidadMean$x ~ puntaCalidadMean$Group.1, type="l", ylab="Beneficio h1", xlab="#Furgonetas")
plot(puntaBenefMean$x ~ puntaBenefMean$Group.1, type="l", ylab="Beneficio h2", xlab="#Furgonetas")
