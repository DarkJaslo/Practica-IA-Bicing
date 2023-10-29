
#Lectura de datos
setwd("C:/Users/Sandra/Documents/Uni/IA/Lab/IA-Practica-Bicing/R")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res4 <- read.table("exp4Final.txt", header = TRUE, sep = "\t")


meanTiempo <- aggregate(x=res4$tiempo, by=list(res4$estaciones), FUN=mean)


#Evolucion de la media del tiempo
plot(meanTiempo$x ~ meanTiempo$Group.1, type="b", ylab="Tiempo (ms)", xlab="Estaciones", xaxt = "n")

axis(1, at = c(25, 50, 75, 100, 125, 150))

options(scipen = 10000)
x <- seq(min(meanTiempo$x), max(meanTiempo$x), length.out=100)

f <- function(y, x) ((x^2)/y)

model <- nls(meanTiempo$Group.1 ~ f(y, meanTiempo$x), data = meanTiempo, start=list(y=10000), trace = TRUE)
summary(model)


f2 <- function(x) (((x^2)/coef(model)))
lines(x,f2(x), col = "red1")

