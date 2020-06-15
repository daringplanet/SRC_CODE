// EncryptData.cpp
/*
Authors:
  John Ortiz
  William Lippard
*/
// This file uses the input data and key information to encrypt the input data
//

#include "Main.h"

/////////////////////////////////////////////////////////////////////////////////////////////////
// code to encrypt the data as specified by the project assignment
int encryptData(char *data, int dataLength)
{










	int resulti = 0;

	gdebug1 = 0;					// a couple of global variables that could be used for debugging
	gdebug2 = 0;					// also can have a breakpoint in C code






	// You can not declare any local variables in C, but should use resulti to indicate any errors
	// Set up the stack frame and assign variables in assembly if you need to do so
	// access the parameters BEFORE setting up your own stack frame
	// Also, you cannot use a lot of global variables - work with registers

	__asm {


		//clearing ecx to create a counter
			xor ecx, ecx

START :

			mov ebx, dataLength  //putting the lenght of the data in edx
			lea edi, gkey   //putting the starting value of gkey into edi
			lea esi, gPasswordHash //putting the starting value of gPasswordHash into esi



			//preserves the number of rounds
			push ecx

			mov edx, data //gets data
			mov ebx, edx  //moves data into ebx
			add ebx, dataLength
			//setting up the hop count
			xor eax, eax
			mov ah, byte ptr[esi + 2 + ecx * 4] //
			mov al, byte ptr[esi + 3 + ecx * 4]
			cmp ax, 0
			je NEWHOP
			mov resulti, eax
CN:	//getting the index
			xor eax, eax
			mov ah, byte ptr[esi + ecx*4]
			mov al, byte ptr[esi + ecx*4 + 1]





NEXT:  //Encyrpting the data with the hash
			xor ecx, ecx
			mov cl, byte ptr[edx]
			xor cl, byte ptr[edi + eax]
			mov byte ptr[edx], cl

			inc edx
			cmp edx, ebx  //checks to see if we're done encrypting data
			je EDONE

			add eax, resulti  //update by the hop count
			cmp eax, 65537  //check boundary conditions
			jb NEXT //repeat until done
			sub eax, 65537
			jmp NEXT



		EDONE :


			//setting up for bit manipulation(DACBE)
			mov ebx, dataLength
			lea esi, gkey
			mov esi, gptrKey
			lea esi, gPassword
			mov esi, gptrPasswordHash


			//clearing the counter
			xor ecx, ecx
			mov edi, data


		START2 :
			//checking to see if we're at the end of the data
		cmp ecx, dataLength
			jge DONE



		D :
		//rotating high nibble left and low nibble right
		xor edx, edx
			xor ebx, ebx
			movzx eax, byte ptr[edi + ecx]
			mov dl, al //gets one byte of the data
			and dl, 0xf0
			clc
			rol dl, 1 //rotating the high nibble left
			jc RH
		RO1 :

		mov bl, al
			and bl, 0x0f
			clc
			ror bl, 1 //rotaing low nibble right
			jc RL
			jmp DDONE

			//checking to see if there was an end bit that was rotated out
		RH :
		and dl, 0xf0
			or dl, 0x10
			jmp RO1

		RL :
		//also checking for end bit but on opposite side
		and bl, 0x0f
			or bl, 0x08
			jmp DDONE

		DDONE :

		xor eax, eax
			add eax, edx
			add eax, ebx
			mov byte ptr[edi + ecx], al


		A :
		movzx eax, byte ptr[edi + ecx]
			rol al, 1 //rotating the bit to to the left
			mov byte ptr[edi + ecx], al


		C :
		//reversing the bit order
		movzx eax, byte ptr[edi + ecx]
			xor ebx, ebx
			xor edx, edx
			//counter to move all the bits
			mov edx, 7
			//loop to rotate all bits until the order is reversed
		LOOP1:
		sal al, 1
			rcr bl, 1  //rotating a bit through the carry
			test edx, edx
			je CDONE
			dec edx
			jmp LOOP1

		CDONE :
		mov al, bl
			mov byte ptr[edi + ecx], al



		B ://swapping nibbles
		xor edx, edx
			xor ebx, ebx
			movzx eax, byte ptr[edi + ecx]
			mov dl, al
			mov bl, al



			and dl, 0xf0 //preserving one of the bits
			ror dl, 4  //rotating the bit to the right 4 times


			and bl, 0x0f //preserving the opposite bit
			ror bl, 4 //rotating it again 4 times


			xor eax, eax
			add eax, ebx
			add eax, edx
			mov byte ptr[edi + ecx], al


		E :   //code table swap



		movzx eax, byte ptr[edi + ecx]
			lea esi, gEncodeTable //gets first value of the table
			movzx al, [esi + eax] //
			mov byte ptr[edi + ecx], al  //swap data with the new bit from ecnrytpion table






			inc ecx
			jmp START2

NEWHOP :  //if its 0 then sets the hop counts to FFFF
			xor eax, eax
			mov eax, 0xffff
			mov resulti, eax
			jmp CN


		DONE :
			pop ecx
				inc ecx
				cmp ecx, gNumRounds
				jne START
				mov resulti, 0

	}



	return resulti;
} // encryptData
