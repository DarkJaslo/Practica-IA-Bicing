
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

meanTiempo <- aggregate(x=res4$tiempo, by=list(res4$estaciones), FUN=mean)


#Evolucion de la media del tiempo
plot(meanTiempo$x ~ meanTiempo$Group.1, type="b", ylab="Tiempo", xlab="Estaciones")

