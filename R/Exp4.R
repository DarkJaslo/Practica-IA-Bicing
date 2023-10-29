###################################IMPORANTE####################################
#Escribe en el siguiente comando el path del directorio donde se encuentre la 
#carpeta y después /R
setwd("")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res4 <- read.table("exp4.txt", header = TRUE, sep = "\t")

meanTiempo <- aggregate(x=res4$tiempo, by=list(res4$estaciones), FUN=mean)

#Evolucion de la media del tiempo
plot(meanTiempo$x ~ meanTiempo$Group.1, type="b", ylab="Tiempo (ms)", xlab="Estaciones", xaxt = "n")

axis(1, at = c(25, 50, 75, 100, 125, 150))