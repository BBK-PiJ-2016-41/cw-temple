# cw-temple

This project contains implementation for the exploration and escape phases of this game.

In the explore phase, I have used a depth-first implementation, modified to prioritise the squares which are closest to the Orb in terms of distance. Although this doesn't offer a perfect direct solution every time, particularly if there are walls in the way which would be better dealt with using a wall-hugging technique, it will finish every time.

In the escape phase, I am exploring in search of gold until such a point is reached where the likelihood of not reaching the exit has increased to a certain level. I have experimented with different thresholds and I've considered that when my estimate of how many moves I have left (time left/average cost of one move) is as low as twice the length of the shortest path from the current point, then I will move for the exit. This strikes a balance with collecting gold and being sure to exit the cavern on time. I have made an exception for exit paths that are less than 10 steps in length, as using a percentage threshold at this small scale will have a higher margin of error, and should go straight to the exit.

I have modified the Explorer class to include solutions to 'explore' and 'escape', and have added methods for calculating the shortest path ('dijkstra'), traversing a given path ('traverse') and picking up gold, including error handling ('pickUpGold').

During testing, I made modifications to the 'TXTmain' class in order to run the code multiple times, but have returned it to the original state.
