package ru.hse.anstkras.terraformkt

/** Element of HCL that can be referenced */
interface HCLNamed {
    val hcl_name: String
    val hcl_ref: String
}