#include<iostream>
using namespace std;

int main()
{
    int length = 0;
    int *Arr = NULL;

    cout<<"Enter ythe number of elements :";
    cin>>length;

    // Step 1: allocate the memory
    Arr = new int [length];
    if(Arr = NULL)
    {
        cout<<"Unable to allocate the memory\n";
    }
    else
    {
        cout<<"Memory gets successfully allocated\n";
    }
    
    // Step 2:
    
    delete [] Arr;  // Avoid the syntax (delete Arr;)


    return 0;
}