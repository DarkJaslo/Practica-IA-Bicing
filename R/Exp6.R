###################################IMPORANTE####################################
#Escribe en el siguiente comando el path del directorio donde se encuentre la 
#carpeta y después /R
setwd("")
if (!require("data.table")) install.packages("data.table")
library(data.table)
res6 <- read.table("exp6.txt", header = TRUE, sep = "\t")

equi <- subset(res6, demanda=="equilibrada")
punta <- subset(res6, demanda=="horaPunta")

boxplot(equi$tiempo, punta$tiempo, names=c("Equilibrada", "Hora punta"), ylab="Tiempo (ms)")

t.test(equi$tiempo, punta$tiempo, paired=TRUE)
