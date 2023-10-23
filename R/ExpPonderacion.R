#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab/IA-Practica-Bicing/R")
if (!require("data.table")) install.packages("data.table")
library(data.table)
resPond <- read.table("exp_ponderacion.txt", header = TRUE, sep = "\t")


pondMean <- aggregate(x=resPond$beneficio+resPond$calidad, by=list(resPond$k), FUN=mean)

#Evolucion suma de beneficios
plot(pondMean$x ~ pondMean$Group.1, type="l", ylab="suma", xlab="k")

