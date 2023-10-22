options(scipen = 10000)


E <- -1
k <- 20
l <- 0.0001

f <- function(t) (exp(E/(k*exp(-l*t))))

t <- 60000
curve(f,from=0,to=t,n=100,ylim=range(0,1))

exp(E/(k*exp(-l*t)))


