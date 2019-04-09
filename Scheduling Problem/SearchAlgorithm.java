import java.util.Random;

public class SearchAlgorithm {

  // Your search algorithm should return a solution in the form of a valid
  // schedule before the deadline given (deadline is given by system time in ms)
  public Schedule solve(SchedulingProblem problem, long deadline) {

    // get an empty solution to start from
    Schedule solution = problem.getEmptySchedule();


    

    return solution;
  }

  // This is a very naive baseline scheduling strategy
  // It should be easily beaten by any reasonable strategy
  public Schedule naiveBaseline(SchedulingProblem problem, long deadline) {

    // get an empty solution to start from
    Schedule solution = problem.getEmptySchedule();

    for (int i = 0; i < problem.courses.size(); i++) {
      Course c = problem.courses.get(i);
      boolean scheduled = false;
      for (int j = 0; j < c.timeSlotValues.length; j++) {
        if (scheduled) break;
        if (c.timeSlotValues[j] > 0) {
          for (int k = 0; k < problem.rooms.size(); k++) {
            if (solution.schedule[k][j] < 0) {
              solution.schedule[k][j] = i;
              scheduled = true;
              break;
            }
          }
        }
      }
    }

    return solution;
  }
  public Schedule naiveBaseline(SchedulingProblem problem) {

    // get an empty solution to start from
    Schedule solution = problem.getEmptySchedule();

    for (int i = 0; i < problem.courses.size(); i++) {
      Course c = problem.courses.get(i);
      boolean scheduled = false;
      for (int j = 0; j < c.timeSlotValues.length; j++) {
        if (scheduled) break;
        if (c.timeSlotValues[j] > 0) {
          for (int k = 0; k < problem.rooms.size(); k++) {
            if (solution.schedule[k][j] < 0) {
              solution.schedule[k][j] = i;
              scheduled = true;
              break;
            }
          }
        }
      }
    }

    return solution;
  }


  // This algorithm mimics the metallurgical process of simulated annealing
  // The number of iterations and schedules it generates is regulated by the cooling
  // rate. The cooling rate for this problem is optimal from rates of 0.01 to 0.4
  // While higher cooling rates do generate higher results the time they take to perform
  // exponentiates
  public Schedule simulatedAnnealing(SchedulingProblem problem, long deadline){
    // Intitial temperature that will be used to control the amount of schedules generated
    double maxTemperature = 10000;
    // Our loop will cease to execute once our max temperature reaches this value
    double minTemperature = 1;
    // This is the cooling rate used to control the amount of shceudules generated
    double coolingRate = 0.3;

    // The idea here is to make a new problem but generate different room and
    // course values to be generated and compared to the current problem
    SchedulingProblem currentProblem = problem;
    // The naive base line schedule we will compare to more optimal solutions
    Schedule currentSchedule = naiveBaseline(problem,deadline);
    
    // While the maximum temperature has not cooled to 1
    while(maxTemperature > minTemperature){
      // Generate a new problem
      SchedulingProblem newProblem = currentProblem;
      // Randomize its parameters
      newProblem.createRandomInstance(currentProblem.buildings.size(),currentProblem.rooms.size(), currentProblem.courses.size());
      // Create a new solution scheudule
      Schedule newSchedule = naiveBaseline(newProblem,deadline);
      
      // Generate the energy produced by the schedules(scores)
      double currentEnergy = problem.evaluateSchedule(currentSchedule);
      double newEnergy = problem.evaluateSchedule(newSchedule);

      // If our new solution is better than the current solution
      if(acceptanceProbability(currentEnergy,newEnergy,maxTemperature) > Math.random()){
        // Current solution becomes the new solution
        currentSchedule = newSchedule;
      }
      // Decrease the max temperature
      maxTemperature *= coolingRate;
    }
    // Return the optimal solution
    return currentSchedule;
  }


  // This method will evaulate the probability of accepting a soltion
  public double acceptanceProbability(double currentEnergy, double neighboringEnergy, double temperature){
      // Once the temperature cools to a certain point we will no longer accept bad solutions
      if(neighboringEnergy > currentEnergy){
        return 1;
      }
      // If the temperature is still very high we might consider it
      return Math.exp((currentEnergy-neighboringEnergy)/temperature);
  }

  // This method will select the fittest idividual from a series of evolved
  // populations based on some fitness criterion
  public Schedule geneticAlgorithm(SchedulingProblem problem, long deadline){
    // We initially generate space for the population (schedules in this case)
    // to store our individuals
    Schedule[] population = new Schedule[8];
    // This loop will select and store the fittest individuals
    // into our population
    for(int i = 0; i <population.length; i++){
      System.out.println("Generation: " + i);
      SchedulingProblem newProblem = problem;
      newProblem.createRandomInstance(problem.buildings.size(),problem.rooms.size(),problem.courses.size());
      population[i] = (naiveBaseline(newProblem));
    }
    // After populating our population, we select the fittest schedule and return it
    return getFittest(evolvePopulation(population,problem),problem);

  }

  public Schedule populate(Schedule gene){
    Random random = new Random();
    for(int i = 0; i < gene.schedule.length; i++){
      for(int j = 0; j < 10; j++){
        gene.schedule[i][j] = random.nextInt(10);
      }
    }
    return gene;
  }
  // This method will take a population and a problem and will return the fittest
  // individual in that population
  public Schedule getFittest(Schedule[] population, SchedulingProblem problem){
    Schedule fittestIndividual = population[0];
    // We iterate over our population and select the fittest individual
    for(int i = 0; i < population.length; i++){
      if(getFitness(problem,population[i]) > getFitness(problem,fittestIndividual)){
        fittestIndividual = population[i];
      }
    }
    return fittestIndividual;
  }
  // This method will use our evaluateSchedule method to return the fitness
  // of an individual in a population
  public double getFitness(SchedulingProblem problem, Schedule genes){
    return problem.evaluateSchedule(genes);
  }
  // Returns the index of a time slot or gene from our individual
  public int getGene(int row, int col, Schedule genes){
    return genes.schedule[row][col];
  }
  // Sets a gene in a given individual
  public void setGene(int row, int col, Schedule genes, int value){
    genes.schedule[row][col] = value;
  }

  // This method will be used to generate populations randomly and crossover their geners
  // into a child
  public Schedule[] evolvePopulation(Schedule[] population, SchedulingProblem problem) {
      // Generate a new population to store the size of the current population
      Schedule[] newPopulation = new Schedule[population.length];
      // Randomly select two of the fittest parents and crossover their features into a child
        for (int index = 0; index < population.length; index++) {
            System.out.println("Current iteration: " + index);
            Schedule firstIndividual = randomSelection(population, problem);
            Schedule secondIndividual = randomSelection(population, problem);
            Schedule newIndividual = crossover(firstIndividual, secondIndividual,problem);
            // Store that child into the new generation
            newPopulation[index] = newIndividual;
        }

        for (int index = 0; index < newPopulation.length; index++) {
           // Mutate random features in a schedule
            mutate(population[index]);
        }

        return newPopulation;
    }

    // This method randomly generates a fit individual from a populaiton
    private Schedule randomSelection(Schedule[] population, SchedulingProblem problem) {
       // Generate the population
        Schedule[] newPopulation = new Schedule[population.length];
        // Populate the population with random indviduals from another population
        for (int index = 0; index < newPopulation.length; index++) {
            int randomIndex = (int) (Math.random() * newPopulation.length);
            newPopulation[index] = population[randomIndex];
        }
        // We then return the fittest of the population that we generated
        Schedule fittestIndividual = getFittest(newPopulation,problem);
        return fittestIndividual;
    }
    
    // This method will generate our children schedules based on the the parents passed in as arguments
    private Schedule crossover(Schedule firstIndividual, Schedule secondIndividual,SchedulingProblem problem) {
        // Some of our randomly generated schedules have different problem spaces, and so we want to iterate
        // over the smallest schedule so that we dont go out of bounds
        Schedule smallestSchedule;
        if(firstIndividual.schedule.length > secondIndividual.schedule.length){
          smallestSchedule = secondIndividual;
        }
        else{
          smallestSchedule = firstIndividual;
        }
        // Initialize a new solution with the space of the smallest schedule
        Schedule newSolution = new Schedule(smallestSchedule.schedule.length,smallestSchedule.schedule[0].length);
        
        // This loop will check compare a random value to a crossover threshold and will swap genes from
        // our parent scheudles if they all within that threshold. The new schedule will then be given the best
        // values from it's parents
        for (int geneIndex = 0; geneIndex < smallestSchedule.schedule.length; geneIndex++) {
            for(int geneIndex2 = 0; geneIndex2 < newSolution.schedule[geneIndex].length ;geneIndex2++){
                if (Math.random() <= 0.31) {
                  setGene(geneIndex,geneIndex2,newSolution,getGene(geneIndex,geneIndex2,firstIndividual));
              } else {
                  setGene(geneIndex,geneIndex2,newSolution,getGene(geneIndex,geneIndex2,secondIndividual));
              }
            }
            
        }
        
        return newSolution;
    }
    
    // This method will assign a random value(gene) to our newly generated solution
    private void mutate(Schedule individual) {
        Random random = new Random();
        for (int geneIndex = 0; geneIndex < individual.schedule.length; geneIndex++) {
            for(int geneIndex2 = 0; geneIndex2 < 10; geneIndex2++){
              if (Math.random() <= 0.15) {
                int gene = random.nextInt(individual.schedule.length);
                setGene(geneIndex,geneIndex2,individual,gene);
              }
            }
        }
    }
  



}
