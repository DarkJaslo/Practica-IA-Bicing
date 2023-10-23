#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab/IA-Practica-Bicing/R")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res6 <- read.table("exp6.txt", header = TRUE, sep = "\t")

equi <- subset(res6, demanda=="equilibrada")
punta <- subset(res6, demanda=="horaPunta")

boxplot(equi$tiempo, punta$tiempo, names=c("Equilibrada", "Hora punta"), ylim=range(0,55),
        xlab= "Demanda", ylab="tiempo")
mean(HCH2$tiempo)
mean(res6$tiempo)
